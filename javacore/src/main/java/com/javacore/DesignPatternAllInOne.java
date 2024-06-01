package com.javacore;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

@Slf4j
public class DesignPatternAllInOne {
    /**
     * 观察者模式
     */
    static class ObserverPattern{
        static class Demo1{
            static class JingDongObserver implements Observer {
                @Override
                public void update(Observable o, Object arg) {
                    log.info("京东上架商品：{}, 来自 {}", arg, o.getClass().getSimpleName());
                }
            }
            static class TaobaoObserver implements Observer {
                @Override
                public void update(Observable o, Object arg) {
                    log.info("淘宝上架商品：{}", arg);
                }
            }
            static class NikeFactory extends Observable{
                private List<String> productsRepo = new ArrayList<>();
                public NikeFactory() {}
                public void signECommerceChannel(Observer observer){
                    //addObserver来自 java.util.Observable.addObserver
                    this.addObserver(observer);
                }
                public void storeProduct(String prod){
                    productsRepo.add(prod);
                    log.info("产品仓入库：{}", prod);
                    //修改Obserable中的状态位
                    this.setChanged();
                    this.notifyObservers(prod);
                }
            }
            public static void main(String[] args) {
                NikeFactory nikeFactory = new NikeFactory();
                TaobaoObserver taobaoObserver = new TaobaoObserver();
                JingDongObserver jdObserver = new JingDongObserver();
                nikeFactory.signECommerceChannel(taobaoObserver);
                //也可以直接调用java API 添加 observer
                nikeFactory.addObserver(jdObserver);

                nikeFactory.storeProduct("yeezy");
                nikeFactory.storeProduct("Heritage Winterized Eugene");
            }
        }
    }
}
