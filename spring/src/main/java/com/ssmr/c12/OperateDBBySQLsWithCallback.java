package com.ssmr.c12;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/com/ssmr/c12/simple-driver-data-source.xml")
public class OperateDBBySQLsWithCallback {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Test
    public void test(){
        Role role = getRoleByConnectionCallback(jdbcTemplate,2);
        Role role1 = getRoleByStatementCallback(jdbcTemplate,3);
        System.out.println(JSON.toJSONString(role));
        System.out.println(JSON.toJSONString(role1));
    }

    public Role getRoleByStatementCallback(JdbcTemplate jdbcTemplate,Integer id){
        Role role = jdbcTemplate.execute(new StatementCallback<Role>() {
            @Override
            public Role doInStatement(Statement stmt) throws SQLException, DataAccessException {
                //Statement应直接传递完整的SQL语句，不能自由设置查询条件的参数
                String sql = "select id,name,age,note from role where id = "+id;
                ResultSet rs = stmt.executeQuery(sql);
                Role temp = null;
                while (rs.next()){
                    temp = new Role();
                    temp.setId(rs.getInt(1));
                    temp.setName(rs.getString(2));
                    temp.setAge(rs.getInt(3));
                    temp.setNote(rs.getString(4));
                }
                return temp;
            }
        });
        return role;
    }

    public Role getRoleByConnectionCallback(JdbcTemplate jdbcTemplate,Integer id){
        Role role = null;
        role = jdbcTemplate.execute(new ConnectionCallback<Role>() {
            Role temp = null;
            @Override
            public Role doInConnection(Connection con) throws SQLException, DataAccessException {
                String sql = "select id,name,age,note from role where id = ?";
                //PrepareStatement可以方便地设置参数
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1,id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    temp = new Role();
                    temp.setId(rs.getInt(1));
                    temp.setName(rs.getString(2));
                    temp.setAge(rs.getInt(3));
                    temp.setNote(rs.getString(4));
                }
                return temp;
            }
        });
        return role;
    }

    @Test //jdbcTemplate出错绝对不会回滚。。
    public void validateJDBCTemplateTransactionFeature(){
        String sql1 = "update role set age = 5 where id = 1";
        String sql2 = "insert into role(name,age,note)values('designer',10,'beauty')";
        String sql3 = "insert into student(name,level)value('jack','primary')";
        String sql4 = "insert into student(name,level)value('jacc','legendary')";
        String sql5 = "insert into student(name,level)value('lucy','junior')";
        ConnCallback callback = new ConnCallback(sql1,sql2,sql3,sql4,sql5);
        Integer executeCount = jdbcTemplate.execute(callback);
        System.out.println(executeCount);
    }

    private class ConnCallback implements ConnectionCallback<Integer>{
        private List<String> sqls;
        private ConnCallback(String... sqlArray){
            this.sqls = Arrays.asList(sqlArray);
        }

        @Override
        public Integer doInConnection(Connection con) throws SQLException, DataAccessException {
            int affectedRows = 0;
            for (String sql : sqls){
                PreparedStatement ps = con.prepareStatement(sql);
                if (sql.contains("jacc")){
                    throw new RuntimeException("test transaction...");
                }
                affectedRows += ps.executeUpdate(sql);
            }
            return affectedRows;
        }
    }
}
