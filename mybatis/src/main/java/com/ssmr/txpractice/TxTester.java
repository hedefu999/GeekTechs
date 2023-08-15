package com.ssmr.txpractice;

import com.ssmr.txpractice.impl.BizService;
import com.ssmr.txpractice.impl.RoleBasicService;
import com.ssmr.txpractice.manager.BusinessManager;
import com.ssmr.txpractice.model.Role;
import com.ssmr.txpractice.model.Student;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMybatisConfig.class)
//declared on class com.ssmr.txpractice.TxTester, attribute 'value' and its alias 'locations' are declared with values of [{...}] and [{+++}], but only one is permitted
//@ContextConfiguration(value = "...",locations = "+++")
public class TxTester {
    private static final Logger log = LoggerFactory.getLogger("TxTester");

    /**
     * 打印出spring究竟注入了哪些bean
     */
    @Autowired
    private ApplicationContext context;
    @Test
    public void testPrintAllbeans(){
        int beanDefinitionCount = context.getBeanDefinitionCount();
        log.info("一共注入了{}个bean", beanDefinitionCount);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames){
            Class<?> type = context.getType(beanName);
            log.info("beanName: {}\nbeanType: {}\nbeanPackage: {}",beanName,type,type.getPackage());
        }
    }
    @Autowired
    private RoleBasicService roleBasicService;
    @Autowired
    private BizService bizService;
    private List<Role> roles = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    @Before
    public void init(){
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 4; i++) {
            Integer second = now.getSecond();
            Role role = new Role();
            role.setId(i);
            role.setName("name"+i+second);
            role.setAge(second+i);
            role.setNote("note"+i+second);
            roles.add(role);

            Student student = new Student();
            student.setId(i);
            student.setLevel(i+"level"+second);
            student.setName(second+"name"+i);
            students.add(student);
        }
    }
    @Test
    public void test(){
        bizService.batchInsertRole(students,roles);
        /**
         * 重点关注debug日志中的 Creating a new SqlSession || Changing isolation level of JDBC Connection
         *
         * 1. @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED)
         *      @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED)
         * 结果：内部方法出错，本次及后面的操作将无法生效
         * 对应的日志：
         * Changing isolation level of JDBC Connection to 2
         *****下面的内容会打印三次
         * Changing isolation level of JDBC Connection to 2
         * Creating a new SqlSession
         * ---保存一条
         * Resetting isolation level of JDBC Connection to 4
         * Returning JDBC Connection to DataSource
         */

        /*
         * 2. @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
         *      @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED)
         * 说明：DEBUG级别日志中可以看出启用类保存点技术，当事务设置为NESTED时，spring会先去探测当前数据库是否支持
         * 如果不支持就采用REQUIRES_NEW的方式，达到避免回滚外部事务的效果
         * 结果：内部方法出错，前面及以后的操作均回滚;多次执行方法id不跳跃
         * 对应的日志：
         * Changing isolation level of JDBC Connection to 2
         * AbstractPlatformTransactionManager:[454] -  Creating nested transaction with name [RoleBasicServiceImpl.saveRole]
         * Creating a new SqlSession
         * Registering transaction synchronization for SqlSession
         *--- 保存一条
         * Releasing transaction savepoint
         * Creating nested transaction with name [RoleBasicServiceImpl.saveRole]
         *--- 保存一条
         * Releasing transaction savepoint
         * Creating nested transaction with name [RoleBasicServiceImpl.saveRole]
         *--- 保存一条
         * AbstractPlatformTransactionManager:[757] -  Releasing transaction savepoint
         * Committing JDBC transaction on Connection
         * Resetting isolation level of JDBC Connection to 4
         * Returning JDBC Connection to DataSource
         */
        /*
         * NESTED(REQUIRES_NEW)默认隔离级别: 提交之前的事务，回滚本次并停止操作；id递增；
         *
         *
         */
    }

    /**
     * 测试@Transactional自调用问题
     */
    @Test
    public void testSelfInvoke(){
        roleBasicService.saveRoles(roles);
    }
    /**
     * 测试错误的异常捕获导致事务策略失败
     */
    @Test
    public void testInserBoth(){
        bizService.insertBoth(students.get(0),roles.get(0));
    }
    /**
     * 展示一种报错 Transaction rolled back because it has been marked as rollback-only
     * 这也是错误的捕获异常引发的
     */
    @Test
    public void testTransactionConflict(){
        bizService.insertBoth2(students.get(0),roles.get(0));
    }
    @Test
    public void testNEST(){
        bizService.insertBoth3(students,roles);
    }

    /**
     * 一连串自调用，事务可以在中间的方法上添加吗？
     */
    @Autowired
    private BusinessManager bizManager;
    @Test
    public void test151() {
        bizManager.operateByType(1L,2);
    }
}
