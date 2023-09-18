package com.allkindsproxy.staticproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloProxy implements IHello {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private IHello iHello;

    public HelloProxy(IHello iHello) {
        this.iHello = iHello;
    }

    @Override
    public void hello(String name) {
        log.info("方法调用前的日志");
        iHello.hello("方法执行");
        log.info("方法执行后的日志");
    }
}
