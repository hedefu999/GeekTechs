package com.ssmr.c09.beanpostprocessor;

public class BeanServiceImpl implements BeanService {
  @Override
  public void printHello() {
    System.out.println("hello");
  }

  @Override
  public String getResult(String input) {
    return "...:"+input;
  }
}
