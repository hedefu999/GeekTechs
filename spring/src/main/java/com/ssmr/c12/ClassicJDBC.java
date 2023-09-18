package com.ssmr.c12;

import java.sql.*;

public class ClassicJDBC {
    public static void main(String[] args) {
        Role role = getRole(2);
        System.out.println(role.getNote());
    }
    public static Role getRole(Integer id){
        Role role = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ssmr","root","...999");
            ps = conn.prepareStatement("select id,`name`,age,note from role where id = ?");
            //Statement statement = conn.createStatement();
            //ResultSet resultSet = statement.executeQuery("select id,`name`,age,note from role where id = 2");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            while (rs.next()){
                role = new Role();
                role.setId(rs.getInt(1));
                role.setName(rs.getString(2));
                role.setAge(rs.getInt(3));
                role.setNote(rs.getString(4));
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        try {
            if (ps != null && !ps.isClosed()){
                ps.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        try {
            if (conn != null && !conn.isClosed()){
                conn.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return role;
    }
}
