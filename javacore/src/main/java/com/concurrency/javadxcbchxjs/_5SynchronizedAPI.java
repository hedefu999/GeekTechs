package com.concurrency.javadxcbchxjs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 第二章 对象及变量的并发访问
 */
public class _5SynchronizedAPI {
    public static long time(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }
    //示例：多个线程共同访问一个对象的方法，方法内部的变量不存在线程安全问题
    static class ThreadSafePrivateField{
        public void addNum(String userName){
            try {
                int num = 0;//方法内部的变量永远都是线程安全的，方法内部的变量是私有的
                if (userName.equals("aa")){
                    num = 100;
                    System.out.println("a set over!");
                    Thread.sleep(2000);
                }else {
                    num = 200;
                    System.out.println("b set over");
                }
                System.out.println(userName+" numn= "+num);
            }catch (InterruptedException e){

            }
        }
        static class ThreadA extends Thread{
            private ThreadSafePrivateField privateField;
            public ThreadA(ThreadSafePrivateField privateField){
                super();
                this.privateField = privateField;
            }
            @Override
            public void run() {
                super.run();
                privateField.addNum("aa");
            }
        }
        static class ThreadB extends Thread{
            private ThreadSafePrivateField privateField;
            public ThreadB(ThreadSafePrivateField privateField){
                super();
                this.privateField = privateField;
            }
            @Override
            public void run() {
                super.run();
                privateField.addNum("bb");
            }
        }
        public static void main(String[] args) {
            ThreadSafePrivateField privateField = new ThreadSafePrivateField();
            ThreadA aThread = new ThreadA(privateField);
            aThread.start();
            ThreadB bThread = new ThreadB(privateField);
            bThread.start();
        }
    }

    /**
     * bb numn= 200
     * aa numn= 100
     */
    //示例：多个线程共同访问一个对象中的实例变量会出现线程安全问题
    //接着上例，将变量num从方法中提出来
    static class HasFieldObject{
        private int num = 0;
        synchronized public void addNum(String add){
            try {
                if (add.equals("aa")){
                    num = 10;
                    Thread.sleep(2000);
                }else {
                    num = 20;
                }
                System.out.println(add + " num = "+num);
            }catch (InterruptedException e){

            }
        }
        static class ThreadA2 extends Thread{
            private HasFieldObject hasFieldObj;
            public ThreadA2(HasFieldObject obj){
                this.hasFieldObj = obj;
            }
            @Override
            public void run() {
                hasFieldObj.addNum("aa");
            }
        }
        static class ThreadB2 extends Thread{
            private HasFieldObject hasFieldOb;
            public ThreadB2(HasFieldObject obj){
                this.hasFieldOb = obj;
            }
            @Override
            public void run() {
                hasFieldOb.addNum("bb");
            }
        }
        public static void main2(String[] args) {
            HasFieldObject fieldObject = new HasFieldObject();
            ThreadA2 threadA2 = new ThreadA2(fieldObject);
            ThreadB2 threadB2 = new ThreadB2(fieldObject);
            threadA2.start();
            threadB2.start();
        }
        /**
         * bb num = 20
         * aa num = 20
         * 打印结果bb的内容被覆盖，而且在bb打印出等待2秒aa才会打印
         * 添加synchronized关键字后内容不会被覆盖，并且是严格按照先aa后bb的顺序打印，但aa bb的打印几乎是同时的
         * aa num = 10
         * bb num = 20
         */
        public static void main(String[] args) {
            //addNum带有synchronized关键字的情况下使用下面的代码
            HasFieldObject fieldObject = new HasFieldObject();
            HasFieldObject fieldObject2 = new HasFieldObject();
            ThreadA2 threadA2 = new ThreadA2(fieldObject);
            ThreadB2 threadB2 = new ThreadB2(fieldObject2);
            threadA2.start();
            threadB2.start();
        }
        /** 将会以异步的方式打印，对象变量不出现线程安全问题，bb打印2秒后出现aa
         * bb num = 20
         * aa num = 10
         * 关键字synchronized取得的锁都是对象锁，而不是把一段代码或方法（函数）当作锁
         * 所以上述示例中，哪个线程先执行带有synchronized关键字的方法，哪个线程就持有该方法所属对象的锁
         * 其他线程就只能呈等待状态，前提是多个线程访问的是同一个对象，最后一个示例是两个线程分别访问两个对象，synchronized体现不出同步执行的效果
         */
    }

    //示例：
    static class OneMethodObject{
        /*synchronized*/public void methodA(){
            String threadName = Thread.currentThread().getName();
            System.out.println("当前线程是 "+threadName);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程即将结束");
        }
        static class ThreadA3 extends Thread{
            private OneMethodObject object;
            public ThreadA3(OneMethodObject obj){
                this.object = obj;
            }
            @Override
            public void run() {
                object.methodA();
            }
        }
        static class ThreadB3 extends Thread{
            private OneMethodObject object;
            public ThreadB3(OneMethodObject obj){
                this.object = obj;
            }
            @Override
            public void run() {
                object.methodA();
            }
        }
        public static void main(String[] args) {
            OneMethodObject object = new OneMethodObject();
            ThreadA3 threadA3 = new ThreadA3(object);
            ThreadB3 threadB3 = new ThreadB3(object);
            threadA3.setName("AA");
            threadB3.setName("BB");
            threadA3.start();
            threadB3.start();
        }/**
         * 方法methodA的synchronized同步关键字可以决定两个线程可否同时进入methodA方法
         * 当前线程是 AA
         * 当前线程是 BB
         * 线程即将结束
         * 线程即将结束
         * --- 添加synchronized关键字后
         * 当前线程是 AA
         * 线程即将结束
         * 当前线程是 BB
         * 线程即将结束
         */
    }

    //示例：一个类中多个方法的情况
    static class TwoMethodObject {
        synchronized public void methodA() {
            try {
                System.out.printf("%s: 当前线程是 %s, 运行方法 %s \n",System.currentTimeMillis()%10000,Thread.currentThread().getName(),"methodA");
                Thread.sleep(2000);
                System.out.printf("%s: 当前线程是 %s, 运行方法 %s 结束 \n",System.currentTimeMillis()%10000,Thread.currentThread().getName(),"methodA");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*synchronized*/ public void methodB(){
            try {
                System.out.printf("%s: 当前线程是 %s, 运行方法 %s \n",System.currentTimeMillis()%10000,Thread.currentThread().getName(),"methodA");
                Thread.sleep(2000);
                System.out.printf("%s: 当前线程是 %s, 运行方法 %s 结束\n",System.currentTimeMillis()%10000,Thread.currentThread().getName(),"methodA");
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        static class ThreadA extends Thread{
            private TwoMethodObject object;
            public ThreadA(TwoMethodObject obj){
                this.object = obj;
            }
            @Override
            public void run() {
                //super.run();
                object.methodA();
            }
        }
        static class ThreadB extends Thread{
            private TwoMethodObject object;
            public ThreadB(TwoMethodObject obj){
                this.object = obj;
            }
            @Override
            public void run() {
                //super.run();
                object.methodB();
            }
        }
        public static void main(String[] args) {
            TwoMethodObject object = new TwoMethodObject();
            ThreadA a = new ThreadA(object);
            ThreadB b = new ThreadB(object);
            a.setName("AA");
            b.setName("BB");
            a.start(); b.start();
        }
        /**
         * 3506: 当前线程是 AA, 运行方法 methodA
         * 3506: 当前线程是 BB, 运行方法 methodA
         * 5532: 当前线程是 AA, 运行方法 methodA 结束
         * 5532: 当前线程是 BB, 运行方法 methodA 结束
         * 虽然线程A先持有了object对象锁，但线程B完全可以异步调用非synchronized类型的方法
         * 如果方法methodB页加上synchronized关键字
         * 6804: 当前线程是 AA, 运行方法 methodA
         * 8824: 当前线程是 AA, 运行方法 methodA 结束
         * 8824: 当前线程是 BB, 运行方法 methodA
         * 828: 当前线程是 BB, 运行方法 methodA 结束
         *
         * 1. A线程先持有object对象的lock锁，B线程可以以异步的方式调用object对象中的非synchronized类型的方法
         * 2. A线程先持有object对象的lock锁，B线程如果在这时调用object对象中的synchronized类型的方法则需要等待，也就是同步
         *
         * 上面TwoMethodObject中如果methodA是修改类变量而methodB是读取类变量的方法，则两个线程分别方法同步的methodA和非同步的methodB方法，就能演示脏读的情况
         */
    }

    //示例：synchronized锁重入 - 在一个synchronized方法/块内部调用本类的其他synchronized方法/块时，是永远可以得到锁的
    static class MultiSyncMethodObject{
        synchronized public void actionA(){
            System.out.println("actionA");
            actionB();
        }
        synchronized public void actionB(){
            System.out.println("acitonB");
        }
    }

    //示例：在父子类继承的环境中也有可重入锁
    static class RentrantParent{
        public int i = 10;
        synchronized public void operateParent(){

        }
    }
    static class RentrantChild extends RentrantParent{
        synchronized public void operateChild(){
            //doSth
            this.operateParent();
        }
    }

    //示例：出现异常时，锁会自动释放
    static class WithExpSyncMethod{
        synchronized public void operate(){
            String threadName = Thread.currentThread().getName();
            if (threadName.equals("AA")){
                try {
                    System.out.printf("%s : %s 线程开始\n", System.currentTimeMillis()%10000,threadName);
                    Thread.sleep(3000);
                    System.out.printf("%s : 3秒到，开始抛出异常\n", System.currentTimeMillis()%10000);
                    int i = 1/0;
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("AA线程仍在执行");
            }else {
                System.out.printf("%s：非A线程在执行", System.currentTimeMillis()%10000);
            }
        }
        static class ThreadA extends Thread{
            private WithExpSyncMethod obj;
            public ThreadA(WithExpSyncMethod obj){
                this.obj = obj;
            }
            @Override
            public void run() {
                obj.operate();
            }
        }
        static class ThreadB extends Thread{
            private WithExpSyncMethod obj;
            public ThreadB(WithExpSyncMethod obj){
                this.obj = obj;
            }
            @Override
            public void run() {
                obj.operate();
            }
        }
        public static void main(String[] args) {
            WithExpSyncMethod method = new WithExpSyncMethod();
            ThreadA a = new ThreadA(method);
            ThreadB b = new ThreadB(method);
            a.setName("AA");
            a.start();
            b.start();
        }
        /** 锁因为异常丢给下家了，下家甚至可以在其打印过程中打印内容
         * 8531 : AA 线程开始
         * 1557 : 3秒到，开始抛出异常
         * Exception in thread "AA" 1557：非A线程在执行java.lang.ArithmeticException: / by zero
         */
    }

    //示例：同步不具有继承性，父类的同步方法在子类中override时不具备同步特性

    //示例：synchronized同步语句块
    /**
     * 用synchronized声明方法的弊端：A线程调用同步方法执行了一个长时任务，B线程就必须等待很长时间，可以使用synchronized同步语句块解决
     * 当两个并发线程访问同一个对象object中的synchronized(this)同步代码块时，一段时间内只能有一个线程被执行，另一个线程必须等待当前线程执行完这个代码块后才能进入
     */
    static class SyncBlockObject{
        public void syncBlockMethod(){
            try {
                synchronized (this){
                    System.out.printf("%s: %s start\n",threadName(), time());
                    Thread.sleep(2000);
                    System.out.printf("%s: %s end\n", threadName(), time());
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        public void syncBlockMethod2(){
            try {
                System.out.printf("%s: %s start\n",threadName(), time());
                synchronized (this){
                    Thread.sleep(2000);
                    System.out.printf("%s: %s end\n", threadName(), time());
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        static class AThread extends Thread{
            private SyncBlockObject object;
            public AThread(SyncBlockObject obj){
                this.object = obj;
            }
            @Override
            public void run() {
                //object.syncBlockMethod();//方法1
                object.syncBlockMethod2();//方法2
            }
        }
        public static void main(String[] args) {
            SyncBlockObject obj = new SyncBlockObject();
            AThread aThread = new AThread(obj);
            AThread bThread = new AThread(obj);
            aThread.setName("AA"); bThread.setName("BB");
            aThread.start(); bThread.start();
        }
        /**使用方法1，不使用synchronized(this)
         * AA: 9679 start
         * BB: 9679 start
         * AA: 1700 end
         * BB: 1700 end
         * 加了同步块 synchronized(this)后
         * AA: 7117 start
         * AA: 9139 end
         * BB: 9140 start
         * BB: 1140 end
         * 使用方法2
         * BB: 9482 start
         * AA: 9482 start //BB线程的start效率高了点，这时方法里一半同步，一半异步的情况
         * BB: 1504 end
         * AA: 3505 end
         * 不再synchronized块中的就是异步执行，在synchronized块中的就是同步执行
         */
    }

    //示例：如果一个类有多个sychronized同步代码块分别位于不同方法中，则多个线程各自访问这些代码块时也要按序执行，synchronized使用的"对象监视器"是一个
    static class MultiSyncBlock{
        public void process(){
            synchronized (this){
                System.out.printf("%s : process\n",time());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void action(){
            synchronized (this){
                System.out.printf("%s : action\n",time());
            }
        }
        static class AThread extends Thread{
            private MultiSyncBlock obj;
            private String methodName;
            public AThread(MultiSyncBlock obj,String methodName){
                this.obj = obj;
                this.methodName = methodName;
            }
            @Override
            public void run() {
                if ("process".equals(methodName)){
                    obj.process();
                }else {
                    obj.action();
                }
            }
        }
        public static void main(String[] args) {
            MultiSyncBlock block = new MultiSyncBlock();
            AThread thread = new AThread(block,"process");
            AThread thread1 = new AThread(block,null);
            thread.start();
            thread1.start();
        }
        /**本示例代码可以验证同步synchronized(this)代码块是锁定当前对象的
         * 7900 : process
         * 9923 : action
         */
    }

    //示例：将任意对象作为监视器
    /**
     * 多个线程调用同一个对象中的不同名称的synchronized同步方法或synchronized(this)同步代码块时，调用的效果就是按顺序执行，也就是同步的阻塞的
     * 同一个时间只有一个线程可以执行synchronized同步方法或synchronized(this)同步代码块中的代码
     * 在多个线程持有"对象监视器"为同一个对象的前提下，同一个时间只有一个线程可以执行synchronized（非this对象x）
     */
    static class SyncUsingAnyObj{
        private String name;
        private String pass;
        //private String lock = new String();//A
        public void setNameAndPass(String name, String pass){
            String lock = new String();//将锁声明在方法内部，替换上面A行的代码
            synchronized (lock){
                System.out.printf("%s: 线程 %s 进入同步块\n", time(), threadName());
                this.name = name;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.pass = pass;
                System.out.printf("%s: 线程 %s 离开同步块\n", time(), threadName());
            }
        }
        static class AThread extends Thread{
            private SyncUsingAnyObj obj;
            private String pass;
            public AThread(SyncUsingAnyObj obj, String pass){
                this.obj = obj;
                this.pass = pass;
            }
            @Override
            public void run() {
                obj.setNameAndPass(Thread.currentThread().getName(),pass);
            }
        }
        public static void main(String[] args) {
            SyncUsingAnyObj obj = new SyncUsingAnyObj();
            AThread thread = new AThread(obj,"world");
            thread.setName("hello");
            AThread thread2 = new AThread(obj, "fool");
            thread2.setName("bar");
            thread.start();
            thread2.start();
        }
        /**
         * 5078: 线程 hello 进入同步块
         * 7102: 线程 hello 离开同步块
         * 7102: 线程 bar 进入同步块
         * 9108: 线程 bar 离开同步块
         * 将锁声明在方法内部，方法就变成了异步的，synchronized(this)与sychronized(非this对象)持有的不是同一把锁
         * 6943: 线程 hello 进入同步块
         * 6943: 线程 bar 进入同步块
         * 8961: 线程 hello 离开同步块
         * 8961: 线程 bar 离开同步块
         *
         * 上述试验说明：同步代码块放在非同步synchronized方法中进行声明，并不能保证调用方法的线程执行同步/异步性
         */
    }

    //示例：
    static class OneItemList{
        private List list = new ArrayList();
        synchronized public void add(String data){
            list.add(data);
        }
        synchronized public int getSize(){
            return list.size();
        }
    }

    static class MyService{
        public OneItemList addItemMethod(OneItemList list, String data){
            if (list.getSize()<1){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.add(data);
            }
            return list;
        }
        static class AThread extends Thread{
            private OneItemList list;
            public AThread(OneItemList list){
                this.list = list;
            }
            @Override
            public void run() {
                MyService service = new MyService();
                service.addItemMethod(list,Thread.currentThread().getName());
            }
        }
        public static void main(String[] args) throws InterruptedException {
            OneItemList list = new OneItemList();
            AThread thread1 = new AThread(list);
            thread1.setName("AA");
            AThread thread2 = new AThread(list);
            thread2.setName("BB");
            thread1.start();
            thread2.start();
            Thread.sleep(3000);//缺少这一行，先面一行总是打印0, 如果此处只睡2000ms，下面一行还可能打印1
            System.out.println(list.getSize());//2
            /**
             * 尽管两个方法都有synchronized关键字加锁，但顺序还是无法保证
             * ??? 解决方案：对addItemMethod方法加锁(x)  对OneItemList对象实例加锁(∫)
             */
        }
    }

    /**
     * 总结
     * 1. 多个线程同时执行synchronized(x){}同步代码块时呈同步效果
     * 2. 其他线程执行x对象中的synchronized同步方法时呈同步效果
     * 3. 当其他线程执行x对象方法中的synchronized关键字的方法时呈同步效果，调用其他非synchronized方法时还是异步调用
     */

    //示例：验证结论2
    static class LockObject{
        synchronized public void printExecutionInfo(){
            System.out.printf("%s: thread name = %s\n",time(),threadName());
            System.out.println("-=-=-=-=-=-=-=-=-=");
        }
    }
    static class ExeService{
        public void exeMethod(LockObject object){
            synchronized (object){
                try {
                    System.out.printf("%s: thread name = %s\n",time(),threadName());
                    Thread.sleep(3000);
                    System.out.printf("%s: thread name = %s\n",time(),threadName());
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    static class ExeWrapper{
        static class AThread extends Thread{
            private ExeService service;
            private LockObject object;
            public AThread(ExeService service, LockObject object){
                this.object = object;
                this.service = service;
            }
            @Override
            public void run() {
                service.exeMethod(object);
            }
        }
        static class BThread extends Thread{
            private LockObject object;
            public BThread(LockObject object){
                this.object = object;
            }
            @Override
            public void run() {
                object.printExecutionInfo();
            }
        }

        public static void main(String[] args) {
            ExeService service = new ExeService();
            LockObject object = new LockObject();
            AThread aThread = new AThread(service,object);
            BThread bThread = new BThread(object);
            aThread.setName("AA");
            bThread.setName("BB");
            //下面两行讲究先后顺序
            bThread.start();
            aThread.start();
        }
        /* 打印效果是同步的
        2119: thread name = AA
        5137: thread name = AA
        5137: thread name = BB
        -=-=-=-=-=-=-=-=-=
         */
    }

    /**
     * synchronized加在static方法与普通方法的影响研究
     * synchronized关键字应用在static方法上表示对当前的*.java文件对应的Class类进行持锁
     */
    static class SyncStaticTest{ //类中共定义了8个方法进行测试
        synchronized public static void methodAStaticSync(){
            try {
                System.out.printf("%s: %s 进入sync static方法A\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出sync static方法A\n",time(),threadName());
            }catch (InterruptedException e){}
        }
        synchronized public static void methodBStaticSync(){
            try {
                System.out.printf("%s: %s 进入static sync方法B\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出static sync方法B\n",time(),threadName());
            }catch (InterruptedException e){}
        }
        public static void methodAStatic(){
            try {
                System.out.printf("%s: %s 进入static方法A\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出static方法A\n",time(),threadName());
            }catch (InterruptedException e){}
        }
        public static void methodBStatic(){
            try {
                System.out.printf("%s: %s 进入static方法B\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出static方法B\n",time(),threadName());
            }catch (InterruptedException e){}
        }
        public void methodA(){
            try {
                System.out.printf("%s: %s 进入普通方法A\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出普通方法A\n",time(),threadName());
            }catch (InterruptedException e){}
        }
        public void methodB(){
            try {
                System.out.printf("%s: %s 进入普通方法B\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出普通方法B\n",time(),threadName());
            }catch (InterruptedException e){}
        }
        synchronized public void methodASync(){
            try {
                System.out.printf("%s: %s 进入sync普通方法A\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出sync普通方法A\n",time(),threadName());
            }catch (InterruptedException e){}
        }
        synchronized public void methodBSync(){
            try {
                System.out.printf("%s: %s 进入sync普通方法B\n",time(),threadName());
                Thread.sleep(3000);
                System.out.printf("%s: %s 将退出sync普通方法B\n",time(),threadName());
            }catch (InterruptedException e){

            }
        }

        static class CThread extends Thread{
            private SyncStaticTest syncStatic;
            public CThread(SyncStaticTest syncStatic){
                this.syncStatic = syncStatic;
            }
            @Override
            public void run() {
                //syncStatic.methodC();
            }
        }
        static class DThread extends Thread{
            private SyncStaticTest syncStatic;
            public DThread(SyncStaticTest syncStatic){
                this.syncStatic = syncStatic;
            }
            @Override
            public void run() {
                syncStatic.methodB();
            }
        }
        static class EThread extends Thread{
            private SyncStaticTest syncStatic;
            public EThread(SyncStaticTest syncStatic){
                this.syncStatic = syncStatic;
            }
            @Override
            public void run() {
                syncStatic.methodAStaticSync();
            }
        }
        static class FThread extends Thread{
            private SyncStaticTest syncStatic;
            public FThread(SyncStaticTest syncStatic){
                this.syncStatic = syncStatic;
            }
            @Override
            public void run() {
                syncStatic.methodBStaticSync();
            }
        }
        static class GThread extends Thread{
            private SyncStaticTest syncStatic;
            public GThread(SyncStaticTest syncStatic){
                this.syncStatic = syncStatic;
            }
            @Override
            public void run() {
                //syncStatic.methodCSync();
            }
        }
        static class HThread extends Thread{
            @Override
            public void run() {
                SyncStaticTest.methodBStaticSync();
            }
        }

        public static void main(String[] args) {
            Thread aStaticSyncThread = new Thread(() -> SyncStaticTest.methodAStaticSync());
            aStaticSyncThread.setName("aStaticSyncThread");
            Thread bStaticSyncThread = new Thread(() -> SyncStaticTest.methodBStaticSync());
            bStaticSyncThread.setName("bStaticSyncThread");
            Thread aStatic = new Thread(() -> SyncStaticTest.methodBStatic());
            aStatic.setName("aStatic");
            Thread bStatic = new Thread(() -> SyncStaticTest.methodBStatic());
            bStatic.setName("bStatic");
            //声明对象
            SyncStaticTest staticTest = new SyncStaticTest();
            Thread aStaticSyncInstanceThread = new Thread(() -> staticTest.methodAStaticSync());
            aStaticSyncThread.setName("aStaticSyncThread");
            Thread aThread = new Thread(() -> staticTest.methodA());
            aThread.setName("aThread");
            Thread bThread = new Thread(() -> staticTest.methodB());
            bThread.setName("bThread");
            Thread aSyncThread = new Thread(() -> staticTest.methodASync());
            aSyncThread.setName("aSyncThread");
            Thread bSyncThread = new Thread(() -> staticTest.methodBSync());
            bSyncThread.setName("bSyncThread");

            SyncStaticTest staticTest2 = new SyncStaticTest();
            Thread aStaticSyncInstanceThread2 = new Thread(() -> staticTest2.methodAStaticSync());
            aStaticSyncInstanceThread2.setName("aStaticSyncInstanceThread2");
            /**
             1. synchronized static方法与synchronized普通方法 不排斥
             aStaticSyncThread.start();
             aSyncThread.start();
             2. 同一把对象锁，sync方法与sync方法互斥
             aSyncThread.start();
             bSyncThread.start();
             3. 同一把对象锁，sync static方法与sync方法 不互斥
             aStaticSyncInstanceThread.start();
             bSyncThread.start();
             看来static方法不论使用类调还是使用对象调，都不会与普通sync方法互斥
             */
            /**
             1. 非同一把对象锁，通过对象调sync static方法依然是互斥的
             aStaticSyncInstanceThread.start();
             aStaticSyncInstanceThread2.start();
             2. 同一个类内synchronized(this)同步代码块与synchronized非static方法是互斥的
             */

        }

    }

    //示例：见识下synchronized(ClassXXX.class)的效果
    static class MultiSyncClassTest{
        public static void methodA(){
            synchronized (MultiSyncClassTest.class){
                try {
                    System.out.printf("%s: %s 进入static innerClassSync方法A\n",time(),threadName());
                    Thread.sleep(3000);
                    System.out.printf("%s: %s 将退出static innerClassSync方法A\n",time(),threadName());
                }catch (InterruptedException e){}
            }
        }
        public static void methodB(){
            synchronized (MultiSyncClassTest.class){
                try {
                    System.out.printf("%s: %s 进入static innerClassSync方法B\n",time(),threadName());
                    Thread.sleep(3000);
                    System.out.printf("%s: %s 将退出static innerClassSync方法B\n",time(),threadName());
                }catch (InterruptedException e){}
            }
        }
        public void methodC(){
            synchronized (MultiSyncClassTest.class){
                try {
                    System.out.printf("%s: %s 进入innerClassSync方法C\n",time(),threadName());
                    Thread.sleep(3000);
                    System.out.printf("%s: %s 将退出innerClassSync方法C\n",time(),threadName());
                }catch (InterruptedException e){}
            }
        }
        public void methodD(){
            synchronized (MultiSyncClassTest.class){
                try {
                    System.out.printf("%s: %s 进入innerClassSync方法D\n",time(),threadName());
                    Thread.sleep(3000);
                    System.out.printf("%s: %s 将退出innerClassSync方法D\n",time(),threadName());
                }catch (InterruptedException e){}
            }
        }
        static class AThread extends Thread{
            private MultiSyncClassTest syncClassTest;
            public AThread(MultiSyncClassTest syncClassTest){
                this.syncClassTest = syncClassTest;
            }
            @Override
            public void run() {
                syncClassTest.methodA();
            }
        }
        static class BThread extends Thread{
            private MultiSyncClassTest syncClassTest;
            public BThread(MultiSyncClassTest syncClassTest){
                this.syncClassTest = syncClassTest;
            }
            @Override
            public void run() {
                syncClassTest.methodB();
            }
        }
        static class CThread extends Thread{
            private MultiSyncClassTest syncClassTest;
            public CThread(MultiSyncClassTest syncClassTest){
                this.syncClassTest = syncClassTest;
            }
            @Override
            public void run() {
                syncClassTest.methodC();
            }
        }
        static class DThread extends Thread{
            private MultiSyncClassTest syncClassTest;
            public DThread(MultiSyncClassTest syncClassTest){
                this.syncClassTest = syncClassTest;
            }
            @Override
            public void run() {
                syncClassTest.methodD();
            }
        }
        static class EThread extends Thread{
            @Override
            public void run() {
                MultiSyncClassTest.methodA();
            }
        }
        static class FThread extends Thread{
            @Override
            public void run() {
                MultiSyncClassTest.methodB();
            }
        }
        public static void main(String[] args) {
            MultiSyncClassTest syncClassTest = new MultiSyncClassTest();
            MultiSyncClassTest syncClassTest2 = new MultiSyncClassTest();
            AThread aThread = new AThread(syncClassTest);aThread.setName("AA");
            AThread aThread2 = new AThread(syncClassTest2);aThread2.setName("AA2");
            //aThread.start();
            //aThread2.start();
            /** 同步执行 即使使用不同类实例也会争抢Class锁
             * 40: AA 进入static innerClassSync方法A
             * 3062: AA 将退出static innerClassSync方法A
             * 3063: AA2 进入static innerClassSync方法A
             * 6064: AA2 将退出static innerClassSync方法A
             */
            BThread bThread = new BThread(syncClassTest);bThread.setName("BB");
            //aThread.start();
            //bThread.start();
            //同步执行
            BThread bThread2 = new BThread(syncClassTest2);bThread.setName("BB2");
            //aThread.start();
            //bThread2.start();
            //同步执行
            CThread cThread = new CThread(syncClassTest);cThread.setName("CC");
            DThread dThread = new DThread(syncClassTest2);dThread.setName("DD");
            //cThread.start();
            //dThread.start();
            //同步执行 不知道通过类示例调static方法做测试有何实际意义。。。。上面的测试比较多余
            EThread eThread = new EThread();eThread.setName("EE");
            FThread fThread = new FThread();fThread.setName("FF");
            eThread.start();
            fThread.start();
            /** 两个线程会争抢内部使用Class加锁的方法的，不论这个方法是否是static的 或者执行者是否是不同的类实例
             * 2527: EE 进入static innerClassSync方法A
             * 5545: EE 将退出static innerClassSync方法A
             * 5546: FF 进入static innerClassSync方法B
             * 8551: FF 将退出static innerClassSync方法B
             */
        }

    }

    //示例：使用String做锁要关注的常量池缓存问题
    static class SyncStringParam {
        public static void print(String strParam){
            try {
                synchronized (strParam){
                    while (true){
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(1000);
                    }
                }
            }catch (InterruptedException e){

            }
        }
        static class AThread extends Thread{
            private SyncStringParam service;
            public AThread(SyncStringParam service){
                this.service = service;
            }
            @Override
            public void run() {
                service.print("AA");
            }
        }
        public static void main(String[] args) {
            SyncStringParam service = new SyncStringParam();
            AThread aThread = new AThread(service);aThread.setName("AA");
            AThread bThread = new AThread(service);bThread.setName("BB");
            aThread.start();
            bThread.start();
        }
        /**
         * 输出结果中只有AA，没有BB，线程BB被阻塞无法执行
         */
    }

    //示例：使用新创建的对象进行同步加锁
    static class SyncNewCreatedObject {
        public void print(Object o){
            try {
                synchronized (o){
                    while(true){
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(1000);
                    }
                }
            }catch (InterruptedException e){

            }
        }
        static class AThread extends Thread{
            private SyncNewCreatedObject syncStringObject;
            public AThread(SyncNewCreatedObject object){
                this.syncStringObject = object;
            }
            @Override
            public void run() {
                syncStringObject.print(new Object());
            }
        }
        public static void main(String[] args) {
            SyncNewCreatedObject service = new SyncNewCreatedObject();
            AThread aThread = new AThread(service);aThread.setName("AA");
            AThread bThread = new AThread(service);bThread.setName("BB");
            aThread.start();
            bThread.start();
        }
        /**
         * 两个线程谁也不阻塞谁，各自隔1秒打印线程名
         */
    }

    //示例：同步方法的死循环
    static class DeadLoop{
        synchronized public void methodA(){
            System.out.println("methodA begin");
            boolean isContinueRun = true;
            while (isContinueRun){
                //A方法永远执行，导致B拿不到锁
            }
            System.out.println("methodA end");
        }
        /**
         * 改进方法是使用两个object对象，分别给A方法和B方法添加同步代码块
         * Object object = new Object();
         * public void methodA2(){
         *     synchronized (object){
         *         System.out.println("methodA begin");
         *         boolean isContinueRun = true;
         *         while (isContinueRun){
         *             //A方法永远执行，导致B拿不到锁
         *         }
         *         System.out.println("methodA end");
         *     }
         * }
         * Object object2 = new Object();
         * public void methodB2(){
         *  synchronized(object2){
         */
        synchronized public void methodB(){
            System.out.println("methodB begin");
            System.out.println("methodB end");
        }
        static class AThread extends Thread{
            private DeadLoop deadLoop;
            public AThread(DeadLoop deadLoop){
                this.deadLoop = deadLoop;
            }
            @Override
            public void run() {
                deadLoop.methodA();
            }
        }
        static class BThread extends Thread{
            private DeadLoop deadLoop;
            public BThread(DeadLoop deadLoop){
                this.deadLoop = deadLoop;
            }
            @Override
            public void run() {
                deadLoop.methodB();
            }
        }
        public static void main2(String[] args) {
            DeadLoop deadLoop = new DeadLoop();
            AThread aThread = new AThread(deadLoop);aThread.setName("AA");
            BThread bThread = new BThread(deadLoop);bThread.setName("BB");
            aThread.start();
            bThread.start();
        }
    }

    //示例：多线程的死锁
    static class DeadLockTest{
        static class DealThread implements Runnable{
            public String userName;
            public Object lock1 = new Object();
            public Object lock2 = new Object();
            public void setFlag(String username){
                this.userName = username;
            }
            @Override
            public void run() {
                if ("jack".equals(userName)){
                    synchronized (lock1){
                        try {
                            System.out.println("userName = "+userName);
                            Thread.sleep(3000);
                        }catch (InterruptedException e){

                        }
                        synchronized (lock2){
                            System.out.println("按 lock1 -> lock2 代码顺序执行");
                        }
                    }
                }
                if ("lucy".equals(userName)){
                    synchronized (lock2){
                        try {
                            System.out.println("userName = "+userName);
                            Thread.sleep(3000);
                        }catch (InterruptedException e){
                            System.out.println("hello world");

                        }
                        synchronized (lock1){
                            System.out.println("按 lock2 -> lock1 代码顺序执行");
                        }
                    }
                }
            }
        }
        //上面那种交叉申请锁的方式跟mysql里悲观死锁产生的一种情况极为相似
        //只要互相等待对方释放锁就有可能出现死锁
        public static void main(String[] args) {
            try {
                DealThread dealThread = new DealThread();

                dealThread.setFlag("jack");
                Thread thread1 = new Thread(dealThread);
                thread1.start();

                Thread.sleep(100);

                dealThread.setFlag("lucy");
                Thread thread2 = new Thread(dealThread);
                thread2.start();

            }catch (InterruptedException e){
                System.out.println("hello world");

            }
        }
        /**
         * 使用JDK自带工具查看线程状态
         * -- 纯命令方式
         * > jps 查看有哪些java进程在运行
         * > jstack -l [pid] 查看对应pid的线程信息，可以看到死锁状况
         * -- 图形工具
         * > jvisualvm 使用图形化工具查看线程状态
         */
    }

    //演示内部类的两个方法在锁互不干扰的情况下杂乱地执行
    static class Inner{
        public void method1(){
            synchronized ("这是一把锁"){
                String name = Thread.currentThread().getName();
                for (int i = 0; i < 10; i++) {
                    System.out.println(name + " i = "+i);
                    try {
                        Thread.sleep(100);
                    }catch (Exception e){}
                }
            }
        }
        public synchronized void method2(){
            String name = Thread.currentThread().getName();
            for (int i = 11; i < 20; i++) {
                System.out.println(name + " i = "+i);
                try {
                    Thread.sleep(100);
                }catch (Exception e){}
            }
        }
        public static void main(String[] args) {
            final Inner inner = new Inner();
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    inner.method1();
                }
            },"A");
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    inner.method2();
                }
            },"B");
            t1.start();
            t2.start();
        }//两个线程的打印先后顺序毫无规律
    }

    static class OutClass{
        static class InnerClass1{
            public void method1(InnerClass2 innerClass2){
                String threadName = Thread.currentThread().getName();
                synchronized (innerClass2){
                    System.out.println(threadName + "进入InnerClass1类中的method1方法");
                    for (int i = 0; i < 10; i++) {
                        System.out.println("i = "+i);
                        try {
                            Thread.sleep(100);
                        }catch (Exception e){}
                    }
                    System.out.println(threadName+" 离开InnerClass1类中的method1方法");
                }
            }
            public synchronized void method2(){
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName+"进入InnerClass1类中的method2方法");
                for (int j = 0; j < 10; j++) {
                    System.out.println("j = "+j);
                    try {
                        Thread.sleep(100);
                    }catch (Exception e){}
                }
                System.out.println(threadName+" 离开InnerClass1类中的method2方法");
            }
        }
        static class InnerClass2{
            public synchronized void method1(){
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName+" 进入InnerClass2类中的method1方法");
                for (int k = 0; k < 10; k++) {
                    System.out.println("k = "+k);
                    try {
                        Thread.sleep(100);
                    }catch (InterruptedException e){}
                }
                System.out.println(threadName+" 离开InnerClass2类中的method1方法");
            }

        }
        public static void main(String[] args) {
            final InnerClass1 in1 = new InnerClass1();
            final InnerClass2 in2 = new InnerClass2();
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    in1.method1(in2);
                }
            },"T1");
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    in1.method2();
                }
            },"T2");
            Thread t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    in2.method1();
                }
            },"T3");
            t1.start();t2.start();t3.start();
        }//T1 T2线程杂乱地互不干扰地执行，T3必须要等T1执行完成后才能执行
    }

    /**
     * @title 2.2.16 锁对象的改变
     * @desc 在将任何数据类型作为同步锁时，多个线程同时持有锁对象就是同步的，分别获得锁对象就是异步的
     */
    static class NotReliableLock{
        static class MethodWrapper{
            private String lock = "123";
            public void testMethod(){
                synchronized (lock){
                    System.out.println(threadName()+" begin "+time());
                    lock = "456";
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {}
                    System.out.println(threadName()+" end "+ time());
                }
            }
        }
        static class ThreadA extends Thread{
            private MethodWrapper methodWrapper;
            public ThreadA(MethodWrapper methodWrapper){
                super();
                this.methodWrapper = methodWrapper;
            }
            @Override
            public void run() {
                methodWrapper.testMethod();
            }
        }
        static class ThreadB extends Thread{
            private MethodWrapper methodWrapper;
            public ThreadB(MethodWrapper methodWrapper){
                super();
                this.methodWrapper = methodWrapper;
            }
            @Override
            public void run() {
                methodWrapper.testMethod();
            }
        }
        public static void main1(String[] args) throws InterruptedException {
            MethodWrapper methodWrapper = new MethodWrapper();
            ThreadA a = new ThreadA(methodWrapper);a.setName("A");
            ThreadB b = new ThreadB(methodWrapper);b.setName("B");
            a.start();
            Thread.sleep(50);
            b.start();
            /**
             * 打印内容
             * A begin 5135
             * B begin 5189
             * A end 7140
             * B end 7189
             * 由于锁被更改，导致B不必等两秒在A执行完再进入，AB线程是异步的
             */
        }
        public static void main(String[] args) {
            MethodWrapper methodWrapper = new MethodWrapper();
            ThreadA a = new ThreadA(methodWrapper);a.setName("A");
            ThreadB b = new ThreadB(methodWrapper);b.setName("B");
            a.start(); b.start();
            /**
             * 打印内容：
             * A begin 93
             * A end 2098
             * B begin 2098
             * B end 4099
             *删除上一例的50ms等待， A B同时执行争抢的锁就是同一把 123 ，AB线程是同步的
             */
        }
    }

    /**
     * 锁对象本身不变，但其字段属性改变,synchronized(锁对象)依然具有互斥效果
     * 但若直接将锁对象的引用换掉，synchronized(锁对象)肯定是锁到了不同的对象上，是非互斥的
     */
    static class ChangingLock{
        @Data @AllArgsConstructor
        static class Lock{
            private Integer number;
            private String name;
        }
        static class MultiThreadHandler{
            private Lock lock = new Lock(1,"locker");
            public void handle(){
                synchronized (lock){
                    System.out.printf("%s - %s\n",time(),threadName());
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) { }
                }
            }
            public void modifyLock(Integer num,String name){
                if (num != null){
                    lock.setNumber(num);
                }
                if (!StringUtils.isEmpty(name)){
                    lock.setName(name);
                }
            }
            public void setLock(Lock lock){
                this.lock = lock;
            }
        }
        public static void main(String[] args) {
            MultiThreadHandler handler = new MultiThreadHandler();
            Thread threadA = new Thread(){
                @Override
                public void run() {
                    handler.handle();
                }
            };threadA.setName("threadA");
            Thread threadB = new Thread(){
                @Override
                public void run() {
                    //操作lock
                    handler.setLock(new Lock(2,"locker2"));
                    //handler.modifyLock(2,"locker2");
                    handler.handle();
                }
            };threadB.setName("threadB");
            threadA.start();
            threadB.start();
        }
    }

}
