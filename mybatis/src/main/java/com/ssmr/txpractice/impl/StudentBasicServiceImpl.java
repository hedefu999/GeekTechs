package com.ssmr.txpractice.impl;

import com.ssmr.txpractice.model.Student;
import com.ssmr.txpractice.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("studentBasicService")
public class StudentBasicServiceImpl implements StudentBasicService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private StudentMapper studentMapper;
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int saveStudent(Student student) {
        int i = studentMapper.insertStudent(student);
        log.info("保存{}条stu数据", i);
        return i;
    }

    @Override
    public Student selectStudent(Integer id) {
        return null;
    }

    @Override
    public int updateStudent(Student student) {
        return studentMapper.updateStudent(student);
    }

    @Override
    public int deleteStudent(Integer id) {
        return studentMapper.deleteStudent(id);
    }
}
