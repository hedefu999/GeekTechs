package com.misc;

/**
 * 卸载了两次对象，第一次执行了 finalize() 方法，成功地把自己从待死亡状态拉了回来；
 * 而第二次同样的代码却没有执行 finalize() 方法，从而被确认为了死亡状态，
 * 这是因为任何对象的 finalize() 方法都只会被系统调用一次。
 */
public class FinalizeTest {
    // 需要状态判断的对象
    public static FinalizeTest Hook = null;
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("执行了 finalize 方法");
        FinalizeTest.Hook = this;
    }
    public static void main(String[] args) throws InterruptedException {
        Hook = new FinalizeTest();
        // 卸载对象，第一次会执行 finalize()
        Hook = null;
        System.gc();
        Thread.sleep(500); // 等待 finalize() 执行
        if (Hook != null) {
            System.out.println("存活状态");
        } else {
            System.out.println("死亡状态");
        }
        // 卸载对象，与上一次代码完全相同
        Hook = null;
        System.gc();
        Thread.sleep(500); // 等待 finalize() 执行
        if (Hook != null) {
            System.out.println("存活状态");
        } else {
            System.out.println("死亡状态");
        }
    }
    /*
     输出内容：
        执行了 finalize 方法
        存活状态
        死亡状态

     虽然可以从 finalize() 方法中把自己从死亡状态“拯救”出来，但是不建议这样做，
     因为所有对象的 finalize() 方法只会执行一次。
     因此同样的代码可能产生的结果是不同的，这样就给程序的执行带来了很大的不确定性。
     */
}
