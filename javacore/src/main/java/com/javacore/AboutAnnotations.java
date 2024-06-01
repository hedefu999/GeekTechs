package com.javacore;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Slf4j
public class AboutAnnotations {
    static class AboutAnnotationInherited{
        /**
         * Inherited: 使用此注解声明出来的自定义注解，表示将来使用InheritedTest的类及其子类会自动继承此注解
         * @Inherited只能用于类，对方法属性无效
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        public @interface InheritedTest {
            String value();
        }
        @Retention(RetentionPolicy.RUNTIME)
        public @interface InheritedTest2 {
            String value();
        }
        @InheritedTest("使用Inherited的注解 class")
        @InheritedTest2("未使用Inherited的注解 class")
        public class Parent {
            @InheritedTest("使用Inherited的注解 method")
            @InheritedTest2("未使用Inherited的注解 method")
            public void method(){
            }
            @InheritedTest("使用Inherited的注解 method2")
            @InheritedTest2("未使用Inherited的注解 method2")
            public void method2(){
            }
            @InheritedTest("使用Inherited的注解 field")
            @InheritedTest2("未使用Inherited的注解 field")
            public String a;
        }
        public class Child extends Parent {
            @Override
            public void method() {
                super.method();
            }
        }
        public static void main(String[] args) {
            Class<Child> child = Child.class;
            log.info("=========打印子类上所有类上的注解");//子类Child继承到一个注解
            log.info("类名：{}.", child.getSimpleName());
            for (Annotation annotation : child.getAnnotations()) {
                log.info(JSON.toJSONString(annotation));
            }
            log.info("=========打印子类上所有方法的注解");//子类Child只有method2上有两个注解
            for (Method method : child.getMethods()) {
                log.info("方法名：{}.", method.getName());
                for (Annotation annotation : method.getAnnotations()) {
                    log.info(JSON.toJSONString(annotation));
                }
            }
            log.info("=========打印子类所有属性上的注解");
            for (Field field : child.getFields()) {
                log.info("字段名：{}.", field.getName());
                for (Annotation annotation : field.getAnnotations()) {
                    log.info(JSON.toJSONString(annotation));
                }
            }
            /**
             * 总结：
             * 子类可以得到父类的Inherited类型的类注解，子类的覆写方法拿不到父类方法上的Interited类型注解
             * Inherited类型注解用在方法和字段上达不到继承的效果，子类只能直接访问父类的方法和字段才能获得注解
             * Inherited类型注解用在类上可以将注解传递给子类
             */
        }
    }
    //包注解
    static class AboutPackageAnnotation{
        @Target(ElementType.PACKAGE)
        @Retention(RetentionPolicy.RUNTIME)
        static @interface PackageDesc{
            String code();
            String desc();
        }
        public static void main(String[] args) {
            Package pack = Package.getPackage("com.javacore");
            Annotation[] annos = pack.getDeclaredAnnotations();
            for (Annotation anno : annos) {
                log.info("{} - {}", anno.annotationType(), anno.toString());
                if (anno.annotationType().equals(PackageDesc.class)){
                    PackageDesc pd = (PackageDesc) anno;
                    log.info(pd.code() + " " + pd.desc());
                }
            }
            //直接获取包注解
            PackageDesc pd = pack.getAnnotation(PackageDesc.class);
            log.info("{} - {}", pd.code(), pd.desc());
        }
    }

}
