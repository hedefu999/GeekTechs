package com.ssmr.txpractice.propagation;

import com.ssmr.txpractice.mapper.RoleMapper;
import com.ssmr.txpractice.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 测试代码应该像笔记一样整合在一个文件里线性展示，如concurrency的staticclass+psvm写法
 * 此处大量使用self实现事务方法整合到一个类里，以便直观测试
 */
@Service("compactPropagation")
public class CompactPropagationService {
    @Autowired
    private CompactPropagationService self;

    /**
     * AMethod{
     *      try{
     *          self.BMethod_TxNEW()
     *          if(something) throw exception
     *      }catch(Exception e){
     *
     *      }
     * }
     * BMethod_TxNEW 能成功提交吗？
     */
    public void test01_AMethod(){
        self.test01_BMethod();
        int i = 1/0;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test01_BMethod(){
        self.updateRoleNoteAppendCurrentSeconds(2);
    }
    /* test01_BMethod事务正常提交
     *
     */



    @Autowired
    private RoleMapper roleMapper;

    public void updateRoleNoteAppendCurrentSeconds(Integer id){
        Role update = new Role();
        update.setId(id);update.setNote("note"+ LocalDateTime.now().getSecond());
        roleMapper.updateRole(update);
    }
}
