package com.ssmr.txpractice.mapper;

import com.ssmr.txpractice.model.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {
    int insertRole(Role role);

    Role selectRole(Integer id);

    int updateRole(Role role);

    int deleteRole(Integer id);

    List<Role> selectByIds(List<Integer> ids);
}
