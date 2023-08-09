package com.drools;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDiscountService{
    @Autowired
    private KieContainer kieContainer;

    public OrderDiscount getDiscount(OrderRequest orderRequest){
        OrderDiscount orderDiscount = new OrderDiscount();
        KieSession kieSession = kieContainer.newKieSession();
        //设置一个全局参数 orderDiscount 保存规则执行结果
        kieSession.setGlobal("orderDiscount", orderDiscount);
        //使用insert方法将请求对象传递给drl文件
        kieSession.insert(orderRequest);
        kieSession.fireAllRules();//触发规则
        kieSession.dispose();//终止会话
        return orderDiscount;
    }
}
