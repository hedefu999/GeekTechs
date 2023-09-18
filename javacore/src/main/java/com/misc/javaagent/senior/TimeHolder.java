package com.misc.javaagent.senior;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于方法计时的一个map包装类
 */
public class TimeHolder {
    private static Map<String, Long> timeCache = new HashMap<>();

    public static void start(String method){
        timeCache.put(method, System.currentTimeMillis());
    }
    public static long cost(String method){
        return System.currentTimeMillis() - timeCache.get(method);
    }
}
