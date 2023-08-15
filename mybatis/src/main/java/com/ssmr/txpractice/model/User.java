package com.ssmr.txpractice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class User {
    private Integer id;
    private String phone;
    private String name;
    private Integer age;
}
