package com.aliasforanno;

import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;

@MyAliasFor(value = "hello",name = "world")
@SimplifiedSpringJunit(prefixName = "HELLO",suffixName = "WORLD")
public class MyAliasForTest {
    @Test
    public void testAliasFor(){
        /**
         * 必须使用spring的AnnotationUtils才能将SimplifiedSpringJunit注解中的错误显示出来
         * AnnotationUtils的1550行hasPlainJavaAnnotationsOnly就是过滤非spring注解的，2137行from方法就是对别名进行处理的
         * 同时使用别名属性且值不一致会在AnnotationUtils.postProcessAnnotationAttributes中抛出异常
         */
        SimplifiedSpringJunit annotation1 = this.getClass().getAnnotation(SimplifiedSpringJunit.class);
        SimplifiedSpringJunit annotation = AnnotationUtils.findAnnotation(getClass(), SimplifiedSpringJunit.class);
        System.out.println(annotation.prefixName());
        System.out.println(annotation.suffixName());
    }
    @Test
    public void testMyAliasFor() {
        /**
         * 自己定义的注解不论是否使用spring的工具都是不会检查别名的规则的，这个要自己实现
         */
        //MyAliasFor annotation = this.getClass().getAnnotation(MyAliasFor.class);
        MyAliasFor annotation = AnnotationUtils.findAnnotation(getClass(), MyAliasFor.class);
        System.out.println(annotation.value());
        System.out.println(annotation.name());
    }

}
