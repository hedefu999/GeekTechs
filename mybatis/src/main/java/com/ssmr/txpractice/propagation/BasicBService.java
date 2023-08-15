package com.ssmr.txpractice.propagation;

import com.ssmr.txpractice.mapper.StudentMapper;
import com.ssmr.txpractice.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("basicBService")
public class BasicBService {
    private final Logger log = LoggerFactory.getLogger(BasicBService.class);
    @Autowired
    private StudentMapper studentMapper;

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIREDNormal(){
        Student student = new Student();
        student.setId(3);
        student.setLevel(CommonUtil.getTimeStr());
        studentMapper.updateStudent(student);
    }
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIREDException(){
        int i = 1/0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIRESNEWNormal(){
        Student student = new Student();
        student.setId(3);
        student.setLevel(CommonUtil.getTimeStr());
        studentMapper.updateStudent(student);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIRESNEWException(){
        int i = 1/0;
    }

    @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateNESTEDNormal(){
        Student student = new Student();
        student.setId(3);
        student.setLevel(CommonUtil.getTimeStr());
        studentMapper.updateStudent(student);
    }
    @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateNESTEDException(){
        int i = 1/0;
    }
}
