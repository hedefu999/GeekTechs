package com.javacore;

import com.utils.SerializeUtils;
import lombok.Data;

import java.io.Serializable;

public class JavaBaseInterfaceDemos {
    /**
     序列化与父子类
     问题：泛化关系的类需要都implements Serializable才能正常正反序列化
     */
    static class ParentChildSerial{
        @Data
        static class Parent{
            private String name;
        }
        @Data
        static class Child extends Parent implements Serializable {
            private static final long serialVersionUID = -6397430691551380678L;
            private Integer age;
            @Override
            public String toString() {
                return "Child{name='" + super.name + "\', age=" + age + '}';
            }
        }
        public static void main(String[] args) {
            String filepath = "novc/parent_child_in_serial2";
            Child child1 = new Child();
            child1.setName("jack");
            child1.setAge(12);
            System.out.println(child1);
            SerializeUtils.serializeObject(child1,filepath);
            Child child = SerializeUtils.deserializeObject(filepath, Child.class);
            System.out.println(child);
            /*
            Child{name='jack', age=12}
            Child{name='null', age=12}
            * */
        }
    }
}
