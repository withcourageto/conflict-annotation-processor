package top.cmoon.tools.anno.processor;

import java.lang.annotation.*;

/**
 * This annotation is aimed to reduce runtime error, and simplify annotation logical processor,
 * usage:
 * <p>
 * <pre>
 *
 * // use @Conflict define annotation
 * {@literal @}Target(ElementType.METHOD)
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 * {@literal @}Conflict({Query.class, Delete.class})
 *  public @interface Update {
 *
 *  }
 *
 *
 * // compiler will check annotation annotated by @Conflict
 *  class UserDao{
 *
 *      // will raise compile error,
 *      // just image there not raise compile error, we read this code, will be confused with the statement,
 *      // we must to find the framework source or document to understand the priority of @Query and @Update,
 *      // then we can sure witch annotation is working
 *      {@literal @}Query
 *      {@literal @}Update
 *       public void updateName(){
 *
 *       }
 *
 *       // will not raise compile error
 *       {@literal @}Update
 *       public void updateAge(){
 *
 *       }
 *
 *  }
 * </pre>
 * <p>
 * Created by cool_moon on 2018/3/13.
 */

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Conflict {


    Class<? extends Annotation>[] value();
}
