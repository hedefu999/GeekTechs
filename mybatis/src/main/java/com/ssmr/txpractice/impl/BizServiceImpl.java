package com.ssmr.txpractice.impl;

import com.ssmr.txpractice.model.Role;
import com.ssmr.txpractice.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("bizService")
public class BizServiceImpl implements BizService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RoleBasicService roleBasicService;
    @Autowired
    private StudentBasicService studentBasicService;
    @Autowired
    private ApplicationContext context;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void batchInsertRole(List<Student> students, List<Role> roleList) {
        int i = 0;
        for(Role role : roleList){
            try {
                i+=roleBasicService.saveRole(role);
            }catch (Exception e){
                log.info("内部方法抛出异常，其他操作不希望被回滚");
            }
        }
        studentBasicService.saveStudent(students.get(0));
        log.info("共保存{}条记录", i);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertBoth(Student student, Role role){
        role.setId(2);
        studentBasicService.saveStudent(student);
        try {
            roleBasicService.saveRole(role);
        }catch (Exception e){
            //为遵守spring约定捕获异常会导致student操作无法回滚
            log.error("异常信息：{}", e.getMessage()+e.getCause());
            throw new RuntimeException(e.getMessage());//强制回滚
        }
    }

    /**
     * insertBoth2作为顶层方法必须有一个自己的事务，以便让roleBasicService去继承
     * 顶层方法的@Transactional不加反而可以正常运行
     * roleBasicService捕获异常使得程序继续执行，但顶层事务沿用下来在异常发生时进行了提交，studentBasicService再来使用就会报rollback-only错
     * studentBasicService事务REQUIRES_NEW也可以避免rollback-only报错
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertBoth2(Student student, Role role) {
        role.setId(2);
        //事务传播属性必须是REQUIRED，目的在于提交掉顶层事务
        try {
            roleBasicService.saveRole(role);
        }catch (Exception e){
        }
        //事务传播属性需要是REQUIRED，事务被提交后再沿用便可报错
        studentBasicService.saveStudent(student);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertBoth3(List<Student> students, List<Role> roles) {
        try {
            roleBasicService.saveRole(roles.get(2));
        }catch (Exception e){
            studentBasicService.saveStudent(students.get(0));
        }
    }

}
