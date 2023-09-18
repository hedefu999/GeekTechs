package com.aliasforanno;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAliasFor {
    @MyAliasFor("name")
    String value() default "hehe";

    @MyAliasFor("value")
    String name() default "hehe";
}
