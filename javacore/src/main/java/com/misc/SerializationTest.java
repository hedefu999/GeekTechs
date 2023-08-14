package com.misc;

import com.jdk.SerializeUtils;
import lombok.Data;

import java.io.*;

/**
 * 序列化的一些疑点
 */
public class SerializationTest {

    /**
    序列化与深拷贝
     问题：深拷贝还要实现一个Clonable接口，序列化可否做到深拷贝的效果？
     序列化和深拷贝都能做到复制一个对象，字段属性一样、hashcode一样，但绝不是同一个对象/引用
     这也演示了存在聚合关系的类都实现Serializable接口后能被完整地正反序列化
     */
    static class SerialDeepClone{

        @Data
        static class Student implements Cloneable, Serializable {
            private static final long serialVersionUID = 7404281399870440435L;
            private String name;
            private Integer age;

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        }
        @Data
        static class Child implements Cloneable,Serializable{
            private static final long serialVersionUID = 5649759893665466061L;
            private String nickName;
            private Student student;

            @Override
            protected Object clone() throws CloneNotSupportedException {
                Child clone = (Child) super.clone();
                clone.student = (Student) student.clone();
                return clone;
            }
        }
        static Child getTestData(){
            Student stu = new Student();
            stu.setName("jack");stu.setAge(12);
            Child child = new Child();
            child.setNickName("jacky");
            child.setStudent(stu);
            return child;
        }
        static void testClone(){
            Child child = getTestData();
            System.out.println("origin child - student -:"+child.hashCode()+" "+child.getStudent().hashCode());
            System.out.println("origin = "+ child);
            try {
                Child clone = (Child) child.clone();
                System.out.println("clone child - student -:"+clone.hashCode()+" "+clone.getStudent().hashCode());
                System.out.println("origin = "+ clone);
                System.out.println("origin == clone? "+ (child == clone)+" "+(child.getStudent()==clone.getStudent()));
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        static void testSerial() throws Exception{
            Child child = getTestData();
            System.out.println("origin child - student -:"+child.hashCode()+" "+child.getStudent().hashCode());
            System.out.println("origin = "+ child);

            File childSerial = new File("novc/childSerial");
            FileOutputStream outputStream = new FileOutputStream(childSerial);
            ObjectOutputStream ooutput = new ObjectOutputStream(outputStream);
            ooutput.writeObject(child);

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(childSerial));
            Child deserial = (Child) ois.readObject();
            System.out.println("clone child - student -:"+deserial.hashCode()+" "+deserial.getStudent().hashCode());
            System.out.println("origin = "+ deserial);
            System.out.println("origin == clone? "+ (child == deserial)+" "+(child.getStudent()==deserial.getStudent()));
        }

        public static void main(String[] args) throws Exception{
            testClone();
            System.out.println("------------------");
            testSerial();
/*
origin child - student -:1849050049 192003594
origin = SerializationTest.SerialDeepClone.Child(nickName=jacky, student=SerializationTest.SerialDeepClone.Student(name=jack, age=12))
clone child - student -:1849050049 192003594
origin = SerializationTest.SerialDeepClone.Child(nickName=jacky, student=SerializationTest.SerialDeepClone.Student(name=jack, age=12))
origin == clone? false false
------------------
origin child - student -:1849050049 192003594
origin = SerializationTest.SerialDeepClone.Child(nickName=jacky, student=SerializationTest.SerialDeepClone.Student(name=jack, age=12))
clone child - student -:1849050049 192003594
origin = SerializationTest.SerialDeepClone.Child(nickName=jacky, student=SerializationTest.SerialDeepClone.Student(name=jack, age=12))
origin == clone? false false
            * */
        }
    }
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
        static class Child extends Parent implements Serializable{
            private static final long serialVersionUID = -6397430691551380678L;
            private Integer age;

            @Override
            public String toString() {
                return "Child{" +
                        "name='" + super.name + '\'' +
                        ", age=" + age +
                        '}';
            }
        }
        static void testDeclareParentButChild(){
            String filepath = "novc/parent_child_in_serial";
            Parent parent = new Child();
            parent.setName("jack");
            ((Child) parent).setAge(12);
            System.out.println(parent);
            SerializeUtils.serializeObject(parent,filepath);
            Child child = SerializeUtils.deserializeObject(filepath, Child.class);
            System.out.println(child);
/*
Child{name='jack', age=12}
Child{name='null', age=12}
            * */
        }
        static void testDeclareParent(){
            String filepath = "novc/parent_child_in_serial2";
            Child child1 = new Child();
            child1.setName("jack");
            child1.setAge(12);
            System.out.println(child1);
            SerializeUtils.serializeObject(child1,filepath);
            Child child = SerializeUtils.deserializeObject(filepath, Child.class);
            System.out.println(child);
            /*打印结果与testDeclareParentButChild相同*/
        }

        public static void main(String[] args) {
            testDeclareParent();
        }
    }
}
