package com.ssmr.txpractice.propagation;

import com.ssmr.txpractice.mapper.UserMapper;
import com.ssmr.txpractice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("basicCService")
public class BasicCService {
    private final Logger log = LoggerFactory.getLogger(BasicCService.class);
    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertUser(User user){
        userMapper.insertUser(user);
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIREDNormal(){
        User user = new User();
        user.setId(3);
        user.setPhone(CommonUtil.getTimeStr());
        userMapper.updateUser(user);
    }
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIREDException(){
        int i = 1/0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIRESNEWNormal(){
        User user = new User();
        user.setId(3);
        user.setPhone(CommonUtil.getTimeStr());
        userMapper.updateUser(user);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateREQUIRESNEWException(){
        int i = 1/0;
    }

    @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateNESTEDNormal(){
        User user = new User();
        user.setId(3);
        user.setPhone(CommonUtil.getTimeStr());
        userMapper.updateUser(user);
    }
    @Transactional(propagation = Propagation.NESTED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public void updateNESTEDException(){
        int i = 1/0;
    }
}
