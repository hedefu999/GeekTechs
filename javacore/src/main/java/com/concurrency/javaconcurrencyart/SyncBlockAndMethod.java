package com.concurrency.javaconcurrencyart;

/**
 * 研究synchronized代码块与方法在字节码层面的差异
 */
public class SyncBlockAndMethod {
    public void syncBlock(){
        synchronized (this){

        }
    }
    public synchronized void syncMethod(){

    }
    /*
     Classfile /Users/hedefu/Documents/DEVELOPER/IDEA/JavaLearning/concurrency/src/main/java/com/concurrency/javaconcurrencyart/SyncBlockAndMethod.class
     Last modified May 5, 2020; size 460 bytes
     MD5 checksum d6d0c3dbd15088934378e0c5a6d1100b
     Compiled from "SyncBlockAndMethod.java"
     public class com.concurrency.javaconcurrencyart.SyncBlockAndMethod
     minor version: 0
     major version: 52
     flags: ACC_PUBLIC, ACC_SUPER
     Constant pool:
     #1 = Methodref          #3.#16         // java/lang/Object."<init>":()V
     #2 = Class              #17            // com/concurrency/javaconcurrencyart/SyncBlockAndMethod
     #3 = Class              #18            // java/lang/Object
     #4 = Utf8               <init>
     #5 = Utf8               ()V
     #6 = Utf8               Code
     #7 = Utf8               LineNumberTable
     #8 = Utf8               syncBlock
     #9 = Utf8               StackMapTable
     #10 = Class              #17            // com/concurrency/javaconcurrencyart/SyncBlockAndMethod
     #11 = Class              #18            // java/lang/Object
     #12 = Class              #19            // java/lang/Throwable
     #13 = Utf8               syncMethod
     #14 = Utf8               SourceFile
     #15 = Utf8               SyncBlockAndMethod.java
     #16 = NameAndType        #4:#5          // "<init>":()V
     #17 = Utf8               com/concurrency/javaconcurrencyart/SyncBlockAndMethod
     #18 = Utf8               java/lang/Object
     #19 = Utf8               java/lang/Throwable
     {
     public com.concurrency.javaconcurrencyart.SyncBlockAndMethod();
     descriptor: ()V
     flags: ACC_PUBLIC
     Code:
     stack=1, locals=1, args_size=1
     0: aload_0
     1: invokespecial #1                  // Method java/lang/Object."<init>":()V
     4: return
     LineNumberTable:
     line 3: 0

     public void syncBlock();
     descriptor: ()V
     flags: ACC_PUBLIC      ---- A
     Code:
     stack=2, locals=3, args_size=1
     0: aload_0
     1: dup
     2: astore_1
     3: monitorenter        ---- B
     4: aload_1
     5: monitorexit     ---- C
     6: goto          14
     9: astore_2
     10: aload_1
     11: monitorexit        ---- D
     12: aload_2
     13: athrow
     14: return
     Exception table:
     from    to  target type
     4     6     9   any
     9    12     9   any
     LineNumberTable:
     line 5: 0
     line 7: 4
     line 8: 14
     StackMapTable: number_of_entries = 2
     frame_type = 255
    offset_delta = 9
    locals = [ class com/concurrency/javaconcurrencyart/SyncBlockAndMethod, class java/lang/Object ]
    stack = [ class java/lang/Throwable ]
    frame_type = 250
    offset_delta = 4

    public synchronized void syncMethod();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED     ---- E
    Code:
    stack=0, locals=1, args_size=1
            0: return
    LineNumberTable:
    line 11: 0
}

     */
    /**
     * 同步代码块带有 monitorenter 和 monitorexit 指令
     *  同步代码块出现两个monitorexit指令的原因：
     *  为了保证抛出异常的情况下也能正常释放锁，javac为同步代码块添加了隐式的try-finally，在finally中会调用monitorexit指令释放锁
     * 同步方法会生成一个ACC_SYNCHRONIZED关键字
     *
     */
}
