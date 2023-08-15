package com.ssmr.txpractice.propagation;

import com.ssmr.txpractice.mapper.RoleMapper;
import com.ssmr.txpractice.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("basicAService")
public class BasicAService {
    private final Logger log = LoggerFactory.getLogger(BasicAService.class);
    @Autowired
    private RoleMapper roleMapper;

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIREDNormal(){
        Role role = new Role();
        role.setId(3);
        role.setNote(CommonUtil.getTimeStr());
        roleMapper.updateRole(role);
    }
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIREDException(){
        int i = 1/0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIRESNEWNormal(){
        Role role = new Role();
        role.setId(3);
        role.setNote(CommonUtil.getTimeStr());
        roleMapper.updateRole(role);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIRESNEWException(){
        int i = 1/0;
    }

    @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateNESTEDNormal(){
        Role role = new Role();
        role.setId(3);
        role.setNote(CommonUtil.getTimeStr());
        roleMapper.updateRole(role);
    }
    @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateNESTEDException(){
        int i = 1/0;
    }
}
