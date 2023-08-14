package com.asm;

import java.util.Date;

public class Teacher {
    private String name;
    private Integer age;
    private Date dutyTime;

    public Teacher(String name, Integer age, Date dutyTime) {
        this.name = name;
        this.age = age;
        this.dutyTime = dutyTime;
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

    public Date getDutyTime() {
        return dutyTime;
    }

    public void setDutyTime(Date dutyTime) {
        this.dutyTime = dutyTime;
    }
}
