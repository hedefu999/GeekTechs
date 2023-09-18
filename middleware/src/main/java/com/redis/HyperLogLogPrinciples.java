package com.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * HyperLogLog实现原理
 */
public class HyperLogLogPrinciples {
    /**
     * 要记录一个数n，就做n次取随机数，计算最大的低位连续0个数，结果maxbits作为2的指数就是n的"近似值"
     */
    static class BitKeeper {
        private int maxbits;
        public void random() {
            long value = ThreadLocalRandom.current().nextLong(2L << 32);
            int bits = lowZeros(value);
            if (bits > this.maxbits) {
                this.maxbits = bits; }
        }
        public void random(long value){
            int bits = lowZeros(value);
            if (bits > this.maxbits){
                this.maxbits = bits;
            }
        }
        private int lowZeros(long value) {
            int i = 1;
            for (; i < 32; i++) {
                //通过移位操作判断数字第i位是不是1
                if (value >> i << i != value) {
                    break;
                }
            }
            return i - 1;
        }
    }
    static void testLowZeroCount(int n){
        for (int j = 1000; j < 100000; j += 100) {
            BitKeeper keeper = new BitKeeper();
            for (int i = 0; i < n; i++) {
                keeper.random();
            }
            System.out.printf("%d %.2f %d\n",n, Math.log(n) / Math.log(2), keeper.maxbits);
        }
    }
    /** 使用2的指数次幂来近似一个数显然误差会很大，改进的方案是进行多次BitKeeper计算，然后进行加权估计 */
    static void moreAccurate(int n){
        int l = 1024;
        BitKeeper[] bitKeepers = new BitKeeper[l];
        for (int i = 0; i < l; i++) {
            bitKeepers[i] = new BitKeeper();
        }
        for (int i = 0; i < n; i++) {
            long m = ThreadLocalRandom.current().nextLong(1L << 32);//m相当于userId
            int index = (int) (((m & 0xfff0000) >> 16) % l);
            bitKeepers[index].random(m);
        }
        double sumbits = 0;
        for (BitKeeper keeper : bitKeepers) {
            sumbits += 1 / (float) keeper.maxbits;
        }
        /**
         * 这里计算调和平均值（倒数的平均）
         * 如 3 4 5 104 的平均值因104的存在抬高到了29
         * 而调和平均值 4/(1/3+1/4+1/5+1/104) 则更合群
         */
        double avgBits = l / sumbits;
        double estimate = Math.pow(2, avgBits) * l;
        System.out.println("更精确的估计值："+ estimate);
    }

    public static void main(String[] args) {
        moreAccurate(400000);
    }

    static class FunnelRateLimiter{
        static class Funnel{
            int capacity;
            float leakingRate;
            int leftQuota;
            long leakingTs;
            public Funnel(int capacity, float leakingRate) {
                this.capacity = capacity;
                this.leakingRate = leakingRate;
                this.leftQuota = capacity;
                this.leakingTs = System.currentTimeMillis();
            }
            //漏斗，就是加水前先来看看漏掉了多少水（时间间隔*速率），把剩余空间更新下
            void makeSpace(){
                long nowTs = System.currentTimeMillis();
                long deltaTs = nowTs - leakingTs;
                float deltaQuota = (int)(deltaTs * leakingRate);
                if (deltaQuota < 0){
                    this.leftQuota = capacity;
                    this.leakingTs = nowTs;
                    return;
                }
                if (deltaQuota < 1){
                    return;
                }
                this.leftQuota += deltaQuota;
                this.leakingTs = nowTs;
                if (this.leftQuota > this.capacity){
                    this.leftQuota = this.capacity;
                }
            }
            //加水，判断剩余空间能否加进去
            boolean watering(int quota){
                makeSpace();
                if (this.leftQuota >= quota){
                    this.leftQuota -= quota;
                    return true;
                }
                return false;
            }
        }
        private Map<String, Funnel> funnels = new HashMap<>();
        public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate){
            String key = String.format("%s:%s", userId, actionKey);
            Funnel funnel = funnels.get(key);
            if (funnel == null){
                funnel = new Funnel(capacity, leakingRate);
                funnels.put(key, funnel);
            }
            return funnel.watering(1);
        }
    }

}
