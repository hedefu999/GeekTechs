package com.concurrency.javaconcurrencyart;

public class _01Start {
    /**
     * 代码并发执行一定比串行快吗？
     * 不一定的，由于存在上下文切换的开销，在count较小（百万级别）时，并发反而比串行慢。如果count很大时才有并发比串行快的情况
     */
    static class ConcurrencyTest {
        private static final long count = 10000l;
        private static void concurrency() throws InterruptedException {
            long start = System.nanoTime();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int a = 0;
                    for (long i = 0; i < count; i++) {
                        a += 5;
                    }
                }
            });
            thread.start();
            int b = 0;
            for (long i = 0; i < count; i++) {
                b--;
            }
            thread.join();
            long time = System.nanoTime() - start;
            System.out.println("concurrency :" + time+"ns,b="+b);
        }
        private static void serial() {
            long start = System.nanoTime();
            int a = 0;
            for (long i = 0; i < count; i++) {
                a += 5;
            }
            int b = 0;
            for (long i = 0; i < count; i++) {
                b--;
            }
            long time = System.nanoTime() - start;
            System.out.println("serial:" + time+"ns,b="+b+",a="+a);
        }
        public static void main(String[] args) throws InterruptedException {
            concurrency();
            serial();
            /**
             * concurrency : 338 1139 ns,b=-10000
             * serial: 24 7112 ns,b=-10000,a=50000
             */
        }
    }
}
