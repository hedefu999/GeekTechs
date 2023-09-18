package com.mybatis.chapter08;

import com.mytest.mybatis.mapper.RoleMapper;
import com.mytest.mybatis.model.Role;
import com.mybatis.utils.JDBCUtils;
import com.mybatis.utils.PropertiesUtils;
import com.mybatis.utils.SqlSessionFactoryUtils;
import com.mysql.jdbc.Driver;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Chapter08Test {
    @Test
    public void simpleQuery(){
        RoleMapper roleMapper = SqlSessionFactoryUtils.getMapper(RoleMapper.class,"mybatis-config.xml");
        PageParam pageParam = new PageParam(1,3,true,true,true,null,null);
        List<Role> roleList = roleMapper.findRoles(pageParam,Arrays.asList("teacher","saler",
                "professor",
                "principal",
                "cashier",
                "accoutant"
        ));
        System.out.println(roleList);
    }
    @Test
    public void testPagePlugin2(){
        RoleMapper roleMapper = SqlSessionFactoryUtils.getMapper(RoleMapper.class,"mybatis-config.xml");
        PageParam pageParam = new PageParam(null,null,true,true,true,null,null);
        List<Role> roles = roleMapper.getRoles4Plugin(pageParam,2);
        System.out.println(roles);
    }

    @Test
    public void testGenericJDBCQuery()throws Exception{
        String jdbcConfig = "/jdbc.properties";
        String sql = "select * from role where role_name = ? and id > ?";
        ResultSet resultSet = JDBCUtils.executeQuery(jdbcConfig,sql,"principal",1);
        while (resultSet.next()){
            System.out.println(resultSet.getString("id"));
        }
    }




















}
