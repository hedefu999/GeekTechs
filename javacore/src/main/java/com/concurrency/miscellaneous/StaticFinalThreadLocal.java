package com.concurrency.miscellaneous;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StaticFinalThreadLocal {
    static class ValueWrapper{
        private Integer userId;
        public Integer get() {
            return userId;
        }
        public void set(Integer userId) {
            this.userId = userId;
        }

        public ValueWrapper(Integer userId) {
            this.userId = userId;
        }
    }
    //ThreadLocal加上static并不能让ThreadLocal中的变量保持单例
    //static final ThreadLocal<Integer> valueWrapper = ThreadLocal.withInitial(()-> 1);
    //这种wrapper肯定是线程不安全的
    ValueWrapper valueWrapper = new ValueWrapper(1);
    @Test
    public void test(){
        List<Integer> lists = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lists.add(i);
        }
        lists.parallelStream().forEach(intVal -> {
            int preVal;
            //全同步起来，线程是安全了，但是花了20s
            synchronized (StaticFinalThreadLocal.class){
                preVal = valueWrapper.get();
                valueWrapper.set(intVal);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //前后的值不一致就说明线程不安全
                log.info("结束 {} - {} - {} - {}", Thread.currentThread().getName(), preVal, intVal, valueWrapper.get());
            }
        });
    }

}
