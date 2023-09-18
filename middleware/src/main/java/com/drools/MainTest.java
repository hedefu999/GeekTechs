package com.drools;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {
    public static void testDrools(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("drools/drools.xml");
        OrderDiscountService orderDiscountService = context.getBean(OrderDiscountService.class);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerNumber("DF78767D34");
        orderRequest.setAge(8);
        orderRequest.setAmount(1200);
        orderRequest.setCustomerType(CustomerType.LOYAL);

        OrderDiscount discount = orderDiscountService.getDiscount(orderRequest);
        System.out.println(discount);
    }
    public static void main(String[] args) {
        testDrools();
    }
}
