package top.cmoon.anno.test;

import top.cmoon.tools.anno.processor.Conflict;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cool_moon on 2018/3/13.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Conflict({Query.class, Delete.class})
public @interface Update {
}
