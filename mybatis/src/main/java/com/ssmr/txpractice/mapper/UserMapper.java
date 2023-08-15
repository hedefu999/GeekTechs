package com.ssmr.txpractice.mapper;

import com.ssmr.txpractice.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int insertUser(User user);
    int updateUser(User user);
    int deleteUser(Integer id);
    User selectUser(Integer id);
}
