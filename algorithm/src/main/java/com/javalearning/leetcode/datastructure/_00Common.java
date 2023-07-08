package com.javalearning.leetcode.datastructure;

import org.junit.Test;

public class _00Common {
    /**
     * 关于递归的一些特点
     * @param a
     * @return
     */
    public int testRecursive(int a){
        if (a == 0){
            return 0;
        }
        int temp = a;
        a = a - 1;
        int areturn = testRecursive(a);//传参写a--会死循环
        System.out.print(areturn);
        //areturn始终是判断条件里返回的0，但如果状态转移方程是testRecursive(a)+1，testRecursive(a)+testRecursive(a-3)
        // 等等形式，这个areturn就是每个子运算的结果
        System.out.print(temp);//想要拿到每次压栈时的a的情况，要使用这里的temp
        return areturn;
    }
    @Test
    public void test14(){
        testRecursive(5);
    }
}
