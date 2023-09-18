package com.ssmr.txpractice.impl;

import com.ssmr.txpractice.model.Role;
import com.ssmr.txpractice.model.Student;

import java.util.List;

public interface BizService {
    void batchInsertRole(List<Student> students, List<Role> roleList);
    void insertBoth(Student student, Role role);

    void insertBoth2(Student student, Role role);

    void insertBoth3(List<Student> students, List<Role> roles);
}
