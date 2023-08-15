package com.ssmr.txpractice.manager;

import com.ssmr.txpractice.impl.RoleBasicService;
import com.ssmr.txpractice.impl.StudentBasicService;
import com.ssmr.txpractice.model.Role;
import com.ssmr.txpractice.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class BusinessManager {
    private final Logger logger = LoggerFactory.getLogger(BusinessManager.class);
    @Autowired
    private RoleBasicService roleBasicService;
    @Autowired
    private StudentBasicService studentBasicService;
    @Autowired
    private BusinessManager self;

    public Integer operateByType(Long id, Integer type){
        switch (type){
            case 2:
                operate2(id);
                return 1;
                default:
                    logger.info("not support");
                    return 0;
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void operate2(Long id){
        self.changeStudnetName();
        // self.changeRoleName();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void changeStudnetName(){
        Student update = new Student();
        update.setId(1);
        int second = LocalDateTime.now().getSecond();
        update.setName("jack"+second);
        studentBasicService.updateStudent(update);
        // self.changeRoleName();
        changeRoleName();
    }

    // @Transactional(propagation = Propagation.REQUIRED)
    public void changeRoleName(){
        Role role = new Role();
        role.setId(2);
        int second = LocalDateTime.now().getSecond();
        role.setName("role"+second);
        roleBasicService.updateRole(role);
        // int i = 1/0;
    }

    /**
     * 事务方法内部连串调用时的情况
     * a operateByType; b operate2; c changeStudnetName; d changeRoleName;
     *
     * a{b}
     * b{c,d}
     * c    d
     * 无事务
     *
     * a{b}
     * B{self.c, self.d}
     * C    D
     * D抛异常会把D回滚掉，但C不会
     *
     * a{self.b}
     * B{self.c, self.d}
     * C D
     * D抛异常，C D都会被回滚。
     * 对比上述两个案例，只要没使用代理访问方法的，上面事务注解就会失效
     * 但再往下走如果能代理了，事务还是会从下面开始生效的
     *
     * a{b}
     * B{self.c}
     * C{self.d}
     * D
     * D抛异常，c d均被回滚
     *
     * a{b}
     * B{self.c}
     * C{d}
     * d
     * d方法就算不声明事务也能被C的事务管理起来
     *
     */
}
