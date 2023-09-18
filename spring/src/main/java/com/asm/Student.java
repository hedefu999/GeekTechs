package com.asm;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Student {
    private String name;
    private Integer age;

    public Student() {
    }

    public Student(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private void repeatContent(String name, Integer number, Date birth){
        System.out.printf("他叫%s，编号%s,是在%s时候到的", name, number, birth);
    }

    public String sayHello2Teacher(String name, Teacher teacher){
        System.out.printf("您好，%s老师,我叫 %s\n",teacher.getName(),name);
        System.out.printf("[系统提示]目标于%s年就职，编号%s\n",new SimpleDateFormat("yyyy-MM-dd").format(teacher.getDutyTime()),teacher.getAge());
        System.out.printf("您好，%s",name);
        return teacher.getName().concat("'s student:").concat(name);
    }
}
