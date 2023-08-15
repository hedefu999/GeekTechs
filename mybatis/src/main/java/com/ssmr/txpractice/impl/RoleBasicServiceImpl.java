package com.ssmr.txpractice.impl;

import com.ssmr.txpractice.model.Role;
import com.ssmr.txpractice.mapper.RoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service("roleBasicService")
public class RoleBasicServiceImpl implements RoleBasicService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private RoleMapper roleMapper;
    @PostConstruct
    public void init(){
        log.info("RoleBasicServiceImpl.init() ... ");
    }

    @Autowired
    private ApplicationContext context;

    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED)
    @Override
    public int saveRoles(List<Role> roleList){
        int i = 0;
        //String roleBasicBeanName = this.getClass().getInterfaces()[0].getSimpleName(); 如何高效地修改字符串第一个字母为小写
        RoleBasicService roleBasicService = (RoleBasicService) context.getBean("roleBasicService");
        for (Role role : roleList){
            /**
             * 下面两行演示@Transaction自调用事务失效的问题
             * 第二种写法是解决方案，但如果将Propagation设置为REQUIRED效果等同于第一行
             */
            //i+=saveRole(role);
            i+=roleBasicService.saveRole(role); //避免@Transactional自调用失效问题
        }
        return i;
    }

    @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED)
    @Override
    public int saveRole(Role role) {
        int i = roleMapper.insertRole(role);
        log.info("保存ROLE【{}】,结果【{}】",role,i);
        //在insert操作之后发生异常回滚会使得自增主键跳过一个
        if (role.getId() == 2){
            i = 1/0;
        }
        return i;
    }

    @Override
    public Role getRoleById(Integer id) {
        Role role = roleMapper.selectRole(id);
        return role;
    }

    @Override
    public int updateRole(Role role) {
        return roleMapper.updateRole(role);
    }

    @Override
    public int deleteRoleById(Integer id) {
        return roleMapper.deleteRole(id);
    }
}
