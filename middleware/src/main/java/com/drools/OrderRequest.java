package com.drools;

import lombok.Data;

@Data
public class OrderRequest {
    private String customerNumber;
    private Integer age;
    //订单金额
    private Integer amount;
    private CustomerType customerType;
}
