package com.concurrency.javadxcbchxjs;

import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class _11Timer {
    static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static long timeStamp(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }
    public static void timeCosumedOperation(){
        long start = System.currentTimeMillis();
        String baidu = "https://www.guancha.cn/";
        try {
            URL url = new URL(baidu);
            URLConnection urlConn = url.openConnection();
            InputStream stream = urlConn.getInputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = stream.read(bytes)) != -1){
                String str = new String(bytes, 0, len);
                if (str.contains("中方")){
                    System.out.println(stream.available());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end - start));
    }

    /**
     * @title 演示Timer对象的用法
     * schedule(TimerTask task, Date time)
     */
    static class SimpleTimerTask{
        public static void main2(String[] args) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("运行时间是："+ new Date());
                }
            };
            LocalDateTime dateTime = LocalDateTime.parse("2020-02-16T18:12:56", formatter);
            timer.schedule(task,Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
        /**
         * timer构造器启动了一个新线程，这个新启动的线程不是守护线程
         * 所以在TimerTask的run方法运行完毕后不会完全退出
         */
        public static void main3(String[] args) throws ParseException {
            Timer timer = new Timer(true);//使用守护线程
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("run at " + new Date());
                }
            };
            Date date = format.parse("2020-02-16 20:45:00");
            timer.schedule(timerTask,date);//timer一次可以shedule多个TimerTask
        }
        /**
         * date如果晚于当前时间，希望run在未来某个时刻，但程序运行后不会运行run方法
         * 反之会立即运行run方法
         * timer一次可以shedule多个TimerTask
         */

        /**
         * schedule(TimerTask task, Date firstTime, long period)
         * 在非守护线程下，在firstTimer时刻到来后，每隔period不断循环执行timertask,如果计划时间早于当前时间，则立刻执行timertask;
         * 对于timertask执行一次的时间t超过period的情况，取t、period两者最大值进行间隔执行；
         */
        public static void main(String[] args) throws ParseException, InterruptedException {
            Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("运行时刻是："+ new Date());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) { }
                }
            };
            Date date = format.parse("2020-02-16 21:18:00");
            timer.schedule(timerTask,date,2000);
            System.out.println("这一句可以被执行到");
            TimeUnit.SECONDS.sleep(10);
            //timertask把自己从任务队列中清除调，控制台不再打印，但不会退出（如果使用守护线程可以退出，但需要保证schedule的date早于启动时间）
            timerTask.cancel();
            timer.schedule(timerTask,2000);//抛出异常
        }
    }

    /**
     * Timer#cancel用于停止任务队列中的所有任务，
     */
    static class TimerCancelFail{
        public static void main(String[] args) throws InterruptedException {
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("任务执行 at " + new Date());
                }
            };
            while (true){
                timer.schedule(timerTask,new Date());
                Thread.sleep(1000);
                timer.cancel();
            }
            /**-- 打印结果：抛出异常
             * 任务执行 at Sun Feb 16 21:49:35 CST 2020
             * java.lang.IllegalStateException: Timer already cancelled.
             */
        }
    }

    /**
     * schedule()不具有追赶执行性，而scheduleAtFixedRate()方法具有追赶执行性。
     */

    /**
     * Timer常用于Android移动开发中，如 todo 类似轮询动画的开发
     */
}
