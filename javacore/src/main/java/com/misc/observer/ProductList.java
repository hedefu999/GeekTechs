package com.misc.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ProductList extends Observable {
    private List<String> productList = null;
    //此类的唯一实例
    private static ProductList instance;
    private ProductList(){}
    public static ProductList getInstance(){
        if (instance == null){
            instance = new ProductList();
            instance.productList = new ArrayList<String>();
        }
        return instance;
    }
    public void addProductListObserver(Observer observer){
        //addObserver()是Observable.java中提供的接口
        this.addObserver(observer);
    }
    public void addProudct(String newProduct) {
        productList.add(newProduct);
        System.out.println("产品列表新增了产品："+newProduct);
        //设置被观察对象发生变化
        this.setChanged();
        //通知观察者，并传递新产品
        this.notifyObservers(newProduct);
    }
    public static void main(String[] args) {
        ProductList observable = ProductList.getInstance();
        TaobaoObserver taobao = new TaobaoObserver();
        JingDongObserver jingdong = new JingDongObserver();
        observable.addProductListObserver(taobao);
        observable.addObserver(jingdong);
        observable.addProudct("铅笔sku");
    }


}