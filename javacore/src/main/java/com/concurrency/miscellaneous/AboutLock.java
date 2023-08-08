package com.concurrency.miscellaneous;

import java.util.concurrent.atomic.AtomicInteger;

public class AboutLock {
/**
    synchronized和ReentrantLock都是可重入锁，那么不可重入锁是谁？
 模拟实现可重入与不可重入锁
*/
    static class ReEntrantLockImple{
        static class Lock{
            volatile boolean isLocked = false;
            volatile Thread lockedBy = null;
            AtomicInteger lockedCount = new AtomicInteger();
            public synchronized void lock()
                    throws InterruptedException{
                Thread thread = Thread.currentThread();
                while(isLocked && lockedBy != thread){
                    wait();
                }
                isLocked = true;
                lockedCount.incrementAndGet();
                lockedBy = thread;
            }
            public synchronized void unlock(){
                if(Thread.currentThread() == this.lockedBy){
                    lockedCount.decrementAndGet();
                    if(lockedCount.get() == 0){//获得该锁的那个线程，获得了多少次该锁（即调用了几次lock方法，即重入了几次），就得unlock几次，即lockedCount=0，才会把那些wait（阻塞）的线程唤醒
                        isLocked = false;
                        notify();
                    }
                }
            }
        }
        public static void main(String[] args) {

        }
    }
    static class NonReEntrantLock{
        static class Lock{
            volatile private boolean isLocked = false;
            public synchronized void lock() throws InterruptedException{
                while(isLocked){
                    wait();//把当前线程wait
                }
                isLocked = true;
            }
            public synchronized void unlock(){
                isLocked = false;
                notify();
            }
        }
        static class Count{
            Lock lock = new Lock();
            public void print(){
                try {
                    lock.lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doAdd();
                lock.unlock();
            }
            public void doAdd(){
                try {
                    lock.lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //do something
                lock.unlock();
            }
        }
    }
}
