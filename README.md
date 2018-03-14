## 注解互斥处理器

Java 注解为我们编程带来了极大的便利，但是在开发的过程中，有时候会有编译注解互斥的需求，Java官方并未实现，
由于JSR 269  Pluggable Annotation Processing API的存在，使得我们有机会能够实现这一需求，所以本人写一个Demo来
实现此功能


## 使用

详细请参看demo

### 前置条件：
* maven
* jdk 1.8


### 引入依赖

```xml
<dependency>
    <groupId>top.cmoon.tools.annotation</groupId>
    <artifactId>conflict-annotation-processor</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 配置编译器

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.7.0</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```
### 使用元注解定义注解
```java

@Conflict({Query.class, Delete.class})  // 定义与 Update 冲突的注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {
    
}


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Delete {
    
}

```
### 编译时报错

```java
public class UserDao {
    
    // 此处将会编译报错
    @Update
    @Delete
    public void getDetail() {
    }
}

```

## 原理
使用```ConflictAnnotationProcessor``` 处理Conflict冲突的发现和报错，此类被作为Processor 的服务实现类，通过SPI被发现










