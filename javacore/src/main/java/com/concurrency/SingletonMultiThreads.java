package com.concurrency;

import java.io.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class SingletonMultiThreads {
    public static long timeStamp() {
        return System.currentTimeMillis() % 10000;
    }

    public static String threadName() {
        return Thread.currentThread().getName();
    }

    /**
     * @case 单例模式的懒汉模式写法存在线程问题
     * 改进方案？
     */
    static class ShowStarveModeNotThreadSafe {
        static class Singleton {
            static Object object;
            public static Object getInstance() {
                if (object == null) {
                    object = new Object();
                }
                return object;
            }
        }
        public static void main(String[] args) {
            //System.out.println(Singleton.getInstance().hashCode());
            //System.out.println(Singleton.getInstance().hashCode());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println(Singleton.getInstance().hashCode());
                }
            };
            for (int i = 0; i < 3; i++) {
                new Thread(runnable).start();
            }
            /**
             * 打开注释就是一样的hashcode
             * 关闭注释就是3个不同的hashcode
             * 解决懒汉模式线程安全问题的一个办法是在getInstance()方法上加synchronized关键字
             */
        }

        /**
         * @case 改进方案1 加synchronized
         * 存在不足：getInstance()加synchronized关键字效率低，因为整个方法都进行了同步
         */
        static class LazySingletonByPartialSync {
            static Object object;
            public static Object getInstance() {
                synchronized (Object.class) {
                    if (object == null) {
                        System.out.println(threadName());
                        object = new Object();
                    }
                }
                //if判断如果放在同步块外面依然存在线程安全问题
                //扩大同步范围跟在方法上加同步效率上几乎没有区别
                return object;
            }
            public static void main(String[] args) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(LazySingletonByPartialSync.getInstance().hashCode());
                    }
                };
                for (int i = 0; i < 3; i++) {
                    new Thread(runnable).start();
                }
            }
        }

        /**
         * @case 使用DCL双检查锁机制(double check lock)
         * 更多资料 todo ref https://www.jianshu.com/p/920d7b18ef25  https://www.ibm.com/developerworks/java/library/j-dcl/index.html#
         * 既能保证不需要同步代码的异步执行性，又能保证单例效果
         * 关键举措：使用volatile，在同步块内再进行一次检查
         */
        static class SingletonByDCL{
            static volatile Object object;
            public static Object getInstance(){
                if (object == null){
                    System.out.println(threadName());
                    synchronized (Object.class){
                        System.out.println("拿到锁："+threadName());
                        if (object == null){
                            System.out.println("初始化："+threadName());
                            object = new Object();
                        }
                    }
                }
                return object;
            }
            public static void main(String[] args) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(SingletonByDCL.getInstance().hashCode());
                    }
                };
                for (int i = 0; i < 3; i++) {
                    new Thread(runnable).start();
                }
            }
            /**
             * Thread-0
             * Thread-2
             * Thread-1
             * 拿到锁：Thread-0
             * 初始化：Thread-0
             * 184894908
             * 拿到锁：Thread-1
             * 184894908
             * 拿到锁：Thread-2
             * 184894908
             */
        }

        /**
         * @case 利用静态内部类持有的实例初始化来实现单例模式
         */
        static class SingletonByStaticInner{
            static class InstanceWrapper{
                private static Object object = new Object();
            }
            public static Object getInstance(){
                return InstanceWrapper.object;
            }
            public static void main(String[] args) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(SingletonByStaticInner.getInstance().hashCode());
                    }
                };
                for (int i = 0; i < 3; i++) {
                    new Thread(runnable).start();
                }
            }
        }

        /**
         * @case 序列化与反序列化的单例模式实现
         * 一个对象经过序列化再反序列化，hashcode是变的，即序列化会破坏单例模式
         * readResolve方法可以替换[反序列化时字节码生成对象这个操作]，也就是直接指定反序列化时拿到的对象，这样JVM里不会有两个SerialData对象
         *
         * - read/writeObject方法与readResolve的区别
         * 如果使用了ObjectInputStream，可以添加readObject(ObjectInputStream) writeObject(ObjectOutputStream)方法进行序列化反序列化
         * 这两个方法可以自定义序列化和反序列化过程中key-value的存放和取出，使用了map
         * readResolve()没有任何参数传入，直接指定一个反序列化结果
         */
        static class SingletonInSerializeEnv{
            static class SerialData implements Serializable{
                private static final long serialVersionUID = -6652178005670761302L;

                protected Object readResolve(){
                    System.out.println("readResolve was invoked");
                    return TestPackage.DataWrapper.data;
                }
            }
            static class TestPackage {
                private static class DataWrapper{
                    private static final SerialData data = new SerialData();
                }
                public static SerialData getInstance(){
                    return DataWrapper.data;
                }
            }
            public static void main(String[] args) throws Exception {
                saveData();
                readData();
            }
            public static void saveData() throws Exception{
                SerialData data = TestPackage.getInstance();
                File datafile = new File("novc/datafile");
                FileOutputStream fos = new FileOutputStream(datafile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(data);
                oos.close();
                fos.close();
                System.out.println(data.hashCode());
            }
            public static void readData() throws Exception{
                FileInputStream fis = new FileInputStream(new File("novc/dataFile"));
                ObjectInputStream ois = new ObjectInputStream(fis);
                SerialData data = (SerialData) ois.readObject();
                ois.close();
                fis.close();
                System.out.println(data.hashCode());
            }
            /**
             * 如果不打开上面的readResolve方法，打印：
             * 1670675563
             * 1922154895
             *
             * 打开上述readResolve方法（方法必须返回Object方法）
             * 1670675563
             * readResolve was invoked
             * 1670675563
             */
        }

        /**
         * @case 使用static代码块实现单例模式
         * 静态代码块中的代码早于类构造器执行,不过似乎变成了饿汉模式
         */
        static class SingletonByStaticBlock{
            static class Wrapper{
                private static Object object = null;
                static {
                    object = new Object();
                }
                public static Object getInstance(){
                    return object;
                }
            }
            public static void main(String[] args) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(Wrapper.getInstance().hashCode());
                    }
                };
                Thread a = new Thread(runnable);
                Thread b = new Thread(runnable);
                a.start();b.start();
            }
        }

        /**
         * @case 使用enum枚举实现单例模式
         * 枚举与静态代码块的特性相似，在使用枚举类时，构造方法会被自动调用，可以应用这个特性实现单例设计模式
         */
        static class SingletonByEnum{
            static enum Wrapper{
                connFactory;
                private Connection connection;
                Wrapper() {
                    try {
                        System.out.println("调用了Wrapper的构造函数");
                        String url = "jdbc:mysql://localhost:3306/ssmr";
                        String userName = "root";
                        String userPass = "...999";
                        String sqlDriver = "com.mysql.cj.jdbc.Driver";
                        Class.forName(sqlDriver);
                        connection = DriverManager.getConnection(url,userName,userPass);
                        System.out.println(connection == null);
                    }catch (Exception e){e.printStackTrace();}
                }
                public Connection getConnection() {
                    return connection;
                }
            }
            public static void main(String[] args) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 2; i++) {
                            System.out.println(Wrapper.connFactory.getConnection().hashCode());
                        }
                    }
                };
                Thread thread1 = new Thread(runnable);
                Thread thread2 = new Thread(runnable);
                thread1.start();thread2.start();
            }

            /**
             * 上述写法将enum connfactory暴露出来，违反"单一职责原则"。。。
             * 所以再套个壳子
             */
            static class EnumWrapper{
                static enum EnumSingleton{
                    connFactory;
                    private Connection conn;
                    private EnumSingleton(){
                        //...
                    }
                    public Connection getConn(){
                        return conn;
                    }
                }
                public static Connection getConnection(){
                    return EnumSingleton.connFactory.getConn();
                }
            }

        }


    }
}
