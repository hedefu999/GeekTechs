package com.ssmr.txpractice.impl;

import com.ssmr.txpractice.model.Student;

public interface StudentBasicService {
    int saveStudent(Student student);

    Student selectStudent(Integer id);

    int updateStudent(Student student);

    int deleteStudent(Integer id);
}
