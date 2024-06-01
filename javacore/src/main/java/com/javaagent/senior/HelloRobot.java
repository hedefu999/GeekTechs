package com.javaagent.senior;

import java.util.concurrent.TimeUnit;

public class HelloRobot {
    public HelloRobot() {
    }
    public static void main(String[] args) {
        TimeHolder.start(args.getClass().getName()+".main");
        HelloRobot helloRobot = new HelloRobot();
        helloRobot.sayHello();
        System.out.println(TimeHolder.cost(args.getClass().getName()+".main"));//[Ljava.lang.String;
    }
    public void sayHello(){
        TimeHolder.start(this.getClass().getName() + ".sayHello");
        System.out.println("hi, people");
        try {
            TimeUnit.SECONDS.sleep((long)(Math.random()*2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.println(TimeHolder.cost(this.getClass().getName()+".sayHello"));
        }
    }
}
