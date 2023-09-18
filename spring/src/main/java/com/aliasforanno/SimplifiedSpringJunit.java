package com.aliasforanno;

import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 为ContextConfiguration中的classes属性增加一个别名注解
 * .@ContextConfiguration注解是@SimplifiedSpringJunit注解的元注解
 * 因为@ContextConfiguration注解本身被定义为@Inherited的，所以@SimplifiedSpringJunit可以被认为继承了@ContextConguration注解
 */
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration
public @interface SimplifiedSpringJunit {
    @AliasFor(value = "classes",annotation = ContextConfiguration.class)
    Class<?>[] classes() default {};

    @AliasFor("suffixName")
    String prefixName() default "";
    @AliasFor("prefixName")
    String suffixName() default "";

    /**
     * 隐式声明一个注解的别名，别名具有传递性
     */
    @AliasFor(annotation = ContextConfiguration.class,attribute = "inheritLocations")
    boolean jichengLocations() default true;
}
