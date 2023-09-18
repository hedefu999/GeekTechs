package com.concurrency.javadxcbchxjs;

public class _12ThreadGroup {
    public static long timeStamp(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }
    public static String groupName(){return Thread.currentThread().getThreadGroup().getName();}
    public static String threadState(){return Thread.currentThread().getState().name();}
    /**
     * 线程组的一级关联
     * ThreadGroup.interrupt();将线程组中每个线程置上中断标记
     */
    static class OneLevelRelation{
        public static void main(String[] args) throws InterruptedException {
            Runnable runnableA = new Runnable(){
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()){
                        System.out.println(threadName());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) { }
                    }
                }
            };
            ThreadGroup firstThreadGroup = new ThreadGroup("我的第一个线程组");
            Thread thread1 = new Thread(firstThreadGroup,runnableA);
            Thread thread2 = new Thread(firstThreadGroup,runnableA);
            thread1.start();thread1.setName("thread1");
            thread2.start();
            System.out.printf("%d - %s",firstThreadGroup.activeCount(),firstThreadGroup.getName());
        }
    }
    /**
     * 线程组的多级关联
     * new ThreadGroup(parentThreadGroup,newThreadGroupName)新建一个ThreadGroup并指定父ThreadGroup和自己的名字
     * new Thread(threadGroup,runnable)新建线程时指定ThreadGroup的名字和Runnale匿名内部类
     * Thread.currentThread().getThreadGroup() 获取当前线程所在的线程组
     * Thread.currentThread().getThreadGroup().activeGroupCount() 获取当前线程所在的线程组中活跃的子线程组数量
     * ThreadGroup#enumerate(ThreadGroup[] groups) 将线程组的子线程数组使用System.arrayCopy拷贝到传入的ThreadGroup数组
     * ThreadGroup#enumerate(ThreadGroup[] groups，boolean) 是否递归查找源线程组的子线程组 拷贝到 线程组数组groups中
     * ThreadGroup#enumerate(Thread[] threads) 将线程组中的所有线程拷贝到threads中
     */
    static class MoreLevelRelation{
        public static void main(String[] args) {
            //将main线程所在的ThreadGroup放入新建的ThreadGroup newGroup中(说反了)
            ThreadGroup tGroup = Thread.currentThread().getThreadGroup();
            ThreadGroup newGroup = new ThreadGroup(tGroup,"A");//新建名称为A的ThreadGroup加入main线程所属的ThreadGroup
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { }
                }
            };
            Thread newThread = new Thread(newGroup,runnable);//newGroup中放入newThread
            newThread.setName("Z");
            newThread.start();

            ThreadGroup[] groups = new ThreadGroup[Thread.currentThread().getThreadGroup().activeGroupCount()];
            Thread.currentThread().getThreadGroup().enumerate(groups);
            System.out.printf("main线程所在ThreadGroup中的子线程组数量：%d, 第一个线程组名称：%s\n",groups.length,groups[0].getName());
            Thread[] threads = new Thread[groups[0].activeCount()];
            groups[0].enumerate(threads);//将线程组内的线程拷贝到数组threads中
            System.out.println(threads[0].getName());
            /*
             * main线程所在ThreadGroup中的子线程组数量：1, 第一个线程组名称：A
             * Z
             */
        }
    }
    /**
     * 线程自动归属特性：线程自动归到当前线程组中
     * 进行new ThreadGroup后ThreadGroup#activeGroupCount返回的数字多了1，新建线程组即使不使用也会加入当前线程组，
     * 这就是自动归类到当前线程组的效果
     */
    static class AutoWrappedInGroup{
        public static void main(String[] args) {
            int activeGroupCount = Thread.currentThread().getThreadGroup().activeGroupCount();
            System.out.printf("线程%s所属的线程组名字：%s，子线程组数量：%d\n",threadName(),groupName(),activeGroupCount);
            ThreadGroup group = new ThreadGroup("新组");//自动加入main组中
            activeGroupCount = Thread.currentThread().getThreadGroup().activeGroupCount();
            System.out.printf("创建线程组后，线程%s所属线程组%s中子线程组数量：%d\n",threadName(),groupName(),activeGroupCount);
            ThreadGroup[] threadGroups = new ThreadGroup[activeGroupCount];
            Thread.currentThread().getThreadGroup().enumerate(threadGroups);
            for (ThreadGroup group1 : threadGroups){
                System.out.println("子线程组名称 "+group1.getName());
            }
            /*
             * 线程main所属的线程组名字：main，子线程组数量：0
             * 创建线程组后，线程main所属线程组main中子线程组数量：1
             * 子线程组名称 新组
             */
        }
    }

    /**
     * main线程组的parent是system（threadGroup.getParent()），再往上就没有父线程组了，threadGroup.getParent()返回null
     */
    static class GetRootThreadGroup{
        public static void main(String[] args) {
            ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
            String name = threadGroup.getName();
            ThreadGroup parent = threadGroup.getParent();
            String name1 = parent.getName();
            ThreadGroup parent1 = parent.getParent();
            System.out.printf("线程组%s的parent %s. %s",name,name1,parent1 == null);//boolean使用%s %b 都可以
            /*
             * 线程组main的parent system. true
             */
        }
    }
    /**
     * 通过线程组批量管理线程：给所有线程设置中断标志
     * 通过new Thread(threadGroup,threadName)设置所属线程组,通过new Thread(threadGroup,Runnable)方式设置的线程组在下述代码中不起作用
     * 原因在于内部的init(group,target,name,0)方法 前者是init(group,null,name,xx),后者是init(group,target,name,0)
     */
    static class BatStopThreadsByThreadGroup{
        static class MyThread extends Thread{
            public MyThread(ThreadGroup group,String threadName){
                super(group,threadName);
            }
            @Override
            public void run() {
                System.out.println(threadName()+"线程运行中");
                while (!this.isInterrupted()){ }
                System.out.println(threadName()+" 即将退出");
            }
        }
        public static void test2() throws InterruptedException {
            ThreadGroup group = new ThreadGroup("myGroup");
            for (int i = 0; i < 5; i++) {
                MyThread thread = new MyThread(group,"name-"+i);
                thread.start();
            }
            Thread.sleep(2000);
            System.out.println("设置中断标志");
            group.interrupt();//通过ThreadGroup批量操作线程：设置中断标志
        }
        public static void test1() throws InterruptedException {
            //lambda一般被编译称静态匿名方法，引用的外部变量以参数方式传递。其中的this指向lambda所在的外部类
            Thread commonThread = new Thread(){
                @Override
                public void run() {
                    System.out.println(threadName()+"线程运行中");
                    while (!this.isInterrupted()){ }
                    System.out.println(threadName()+" 即将退出");
                }
            };
            ThreadGroup group = new ThreadGroup("myGroup");
            for (int i = 0; i < 5; i++) {
                //两种写法都不行。。。线程组不要这样用
                //Thread thread = new Thread(group,commonThread);
                Thread thread =null;
                new Thread(group,thread = new Thread(){
                    @Override
                    public void run() {
                        System.out.println(threadName()+"线程运行中");
                        while (!this.isInterrupted()){ }
                        System.out.println(threadName()+" 即将退出");
                    }
                });
                thread.start();
            }
            Thread.sleep(2000);
            System.out.println("设置中断标志");
            group.interrupt();//通过ThreadGroup批量操作线程：设置中断标志
        }
        public static void main(String[] args) throws InterruptedException {
            test1();
            /*
             * Thread-1线程运行中
             * Thread-3线程运行中
             * Thread-4线程运行中
             * Thread-2线程运行中
             * Thread-5线程运行中
             * 设置中断标志
             */
            //test2();
            /*
             * name-0线程运行中
             * name-3线程运行中
             * name-2线程运行中
             * name-1线程运行中
             * name-4线程运行中
             * 设置中断标志
             * name-4 即将退出
             * name-1 即将退出
             * name-2 即将退出
             * name-0 即将退出
             * name-3 即将退出
             */
        }
    }





}
