package com.mytest.mybatis.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;

//@Data
//@AllArgsConstructor
@NoArgsConstructor
public class UserInfo{
    private int id;
    private String userName;
    private Integer age;
    private String signDesc;
    private LocalDate birth;
    //private Date birth;
    private BigDecimal balance;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    //private Timestamp createTime;
    //private Timestamp updateTime;

    public UserInfo(int id, String userName, Integer age, String signDesc, LocalDate birth, BigDecimal balance, LocalDateTime createTime, LocalDateTime updateTime) {
        System.out.println("...: all args constructor was invoked!");
        this.id = id;
        this.userName = userName;
        this.age = age;
        this.signDesc = signDesc;
        this.birth = birth;
        this.balance = balance;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    //public static UserInfo getInstantce(String userName, Integer age, String signDesc, LocalDate birth, BigDecimal balance, LocalDateTime createTime, LocalDateTime updateTime){
    //    UserInfo userInfo = new UserInfo(0,userName, age, signDesc, birth, balance, createTime, updateTime);
    //    userInfo.setUserName(userName);
    //    userInfo.setAge(age);
    //    userInfo.setSignDesc(signDesc);
    //    userInfo.setBirth(birth);
    //    userInfo.setBalance(balance);
    //    userInfo.setCreateTime(createTime);
    //    userInfo.setUpdateTime(updateTime);
    //    return userInfo;
    //}

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                ", signDesc='" + signDesc + '\'' +
                ", birth=" + birth +
                ", balance=" + balance +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
