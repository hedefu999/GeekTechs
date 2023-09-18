package com.ssmr.txpractice.propagation;

import com.ssmr.txpractice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component("propagation")
public class PropagationService {
    private final Logger log = LoggerFactory.getLogger(PropagationService.class);

    @Autowired
    private BasicAService basicAService;
    @Autowired
    private BasicBService basicBService;
    @Autowired
    private BasicCService basicCService;
    /**
      method_no_txManager(){
        a_method_REQUIRES_NEW_normal();
        b_method_REQUIRES_NEW_exception();
      }
     问题：a_method会被回滚吗？
     答案：不会，user正常插入
     */
    public void testTwoREQUIRESNEWinNormalMethod(){
        basicCService.insertUser(new User(null, "jack", "12333",12));
        log.info("执行下一个将抛出异常的方法");
        basicAService.updateREQUIREDException();
    }

    /**
     * 验证REQUIRED的事务异常透传效果：即便是通过trycatch捕获，外层事务也会被回滚
     *
     * method_REQUIRES_NEW_Normal(){
     *     try{
     *         A#method_REQUIRED_exception();
     *     }catch(Exception e){}
     *
     *     B#method_REQUIRED_Normal();
     *
     * }
     * 打印结果：
     * 抛出异常 Transaction rolled back because it has been marked as rollback-only
     * 代码即便是有trycatch也出现异常
     * 由AbstractPlatformTransactionManager抛出
     * A B均回滚
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void testREQUIREDTxUpNotify(){
        try {
            basicAService.updateREQUIREDException();
        }catch (Exception e){}
        basicCService.updateREQUIREDNormal();
    }

    /**
     * 验证NESTED的不影响上层事务的效果,展示NESTED的正确用法
     *
     * method_REQUIRES_NEW_Normal(){
     *
     *   A#method_REQUIRED_Normal();
     *
     *   try{
     *       B#method_NESTED_exception();
     *   }catch(Exception e){}
     *
     * }
     * 只要捕获好，代码日志里就没有异常抛出，A提交 B回滚
     * anchor:/ by zero
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void testNESTEDFunction(){
        basicAService.updateREQUIREDNormal();
        try {
            basicBService.updateNESTEDException();
        }catch (Exception e){
            System.out.println("anchor:"+e.getMessage());
        }
    }

    /**
     * NESTED用起来必须trycatch
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void wrongWayNESTEDFunction(){
        basicAService.updateREQUIREDNormal();
        basicBService.updateNESTEDException();
    }

    /**
     * 混合自调用的情况
     class A{
        A#method_REQUIRES_NEW_Normal_1(){
             try{
                A#method_REQUIRED_Exception_3();
             }catch(){}

             try{
                B#method_REQUIRED_Normal();
             }catch(){}

         }
         A#method_REQUIRED_Normal_2(){}
         A#method_REQUIRED_Exception_3();
     }

     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void hybridSelfInvokeAndOtherRequired(){
        try {
            externalREQUIREDException();
        }catch (Exception e){}
        try {
            basicCService.updateREQUIREDNormal();
        }catch (Exception e){}
    }

    @Autowired
    private ApplicationContext context;
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void hybridAntiSelfInvokeAndOtherRequired(){
        try {
            PropagationService propagation = (PropagationService) context.getBean("propagation");
            propagation.externalREQUIREDException();
        }catch (Exception e){}
        try {
            basicCService.updateREQUIREDNormal();
        }catch (Exception e){}
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void externalREQUIREDNormal(){
        basicBService.updateREQUIREDNormal();
    }
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void externalREQUIREDException(){
        int i = 1/0;
    }

}
