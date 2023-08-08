package com.misc.observer;

import java.util.Observable;
import java.util.Observer;

public class JingDongObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        String newProduct = (String) arg;
        System.err.println("发送新产品【"+newProduct+"】同步到京东商城");
    }
}
