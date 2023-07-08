package com.javalearning.freeresearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.stream.Stream;

/**
 * 缓存淘汰策略相关算法
 */
public class CacheEliminationStrategy {

    /**
      LRU 算法的实现
     缓存大小上限为3，只能存3个key
     输入内容：[[1,1,1],[1,2,2],[1,3,2],[2,1],[1,4,4],[2,2]],3
        上述输入最后的3表示缓存空间大小
        前面代表操作序列：
        存入(1,1),
        存入(2,2),最不常用key=1,[1,2]
        存入(3,2),最不常用key=1,[1,2,3]
        读取(1),  得到1，最不常用key=2,[2,3,1]
        存入(4,4),缓存空间满，移除目前最不常用的2，[3,1,4]
        读取(2),key=2已被删除
     输出：[1,-1] （两次读取key得到的返回结果，第二次key已被移除，返回-1）
     */
    static class SimpleLRUScheme{
        public String solution(int[][] operators, int k){
            Map<Integer,Integer> map = new LinkedHashMap<>();
            List<Integer> result = new LinkedList<>();
            for (int[] operation : operators){
                int opType = operation[0];
                int key = operation[1];
                switch (opType){
                    case 1://写入
                        int value = operation[2];
                        if (map.size()>=k){
                            Iterator<Integer> iterator = map.keySet().iterator();
                            map.remove(iterator.next());//淘汰最早的key，map.entrySet().iterator().remove()使不得
                        }
                        map.put(key, value);
                        break;
                    case 2:
                        if (map.containsKey(key)){
                            Integer getValue = map.get(key);
                            result.add(getValue);
                            map.remove(key);map.put(key,getValue);//存在的key被读取到如何升级为常用
                        }else {
                            result.add(-1);
                        }
                        break;
                }
            }
            return result.toString();
        }
        //LinkedHashMap自带LRU效果，上面写法缺乏对LinkedHashMap accessOrder属性的认知
        //也可以自己写一个以HashMap为缓存，额外维护一个链表保存key，这样就不用每次获取迭代器了，相当于重写LinkedHashMap，实现案例 https://www.jianshu.com/p/62e829c37adf
        public static synchronized void solution2(int[][] operators, int size){
            LinkedHashMap<Integer, Integer> map = new LinkedHashMap<Integer, Integer>(16,0.75f,true);
            //Iterator<Integer> iterator = map.keySet().iterator();//迭代器写外面就low了
            for (int[] operation : operators){
                int opType = operation[0];
                int key = operation[1];
                switch (opType) {
                    case 1://写入
                        int value = operation[2];
                        if (map.size() >= size){//LinkHashMap remove一次再add一次，这样就可以将key放在链表末尾，最不可能被删除
                            map.remove(map.keySet().iterator().next());
                        }
                        map.put(key, value);
                        break;
                    case 2:
                        System.out.println(map.get(key));
                        break;
                }
            }
        }

        /**
         实际的LRU缓存可以使用Redis List+hash实现，hash保证O(1)的get和set，List提供O(1)的顺序调整
         Redis的链表能存储2^32-1个节点，超过40亿个，而且是双向的
         */
        public static void main(String[] args) {
            int[][] operators = {{1,1,1},{1,2,2},{1,3,2},{2,1},{1,4,4},{2,2}};
            solution2(operators,3);
        }

    }

    /**
     LFU（least frequently used）
     LRU最近使用了一次就被认为应该保留，而按节点访问次数确定哪些应该保留更符合习惯，这就是LFU
     实现方案1：
     用双向链表(数组？)形成多个桶，每个桶按序表示使用次数为1，2，…，在每个桶里按使用先后挂链。
     容量满需要删除时就删除桶上的链中最旧的节点
     访问次数通常认为不会太高，或者设置上限
     实现方案2:
     两个HashMap，一个存储key,val - contentMap,一个存储freq,keys - freqMap
     加变量currMinFreq,就不用遍历freqMap的freq了
     freq的keys需要O(1)地add、get、remove,（LinkedList、ArrayList、LinkedHashSet）中只能选LinkedHashSet
     */
    static class SimpleLFUScheme{
        //ref 算法题就像搭乐高：手把手带你拆解 LFU 算法
        static class LabuladongLFU{
            interface ILFUCache{
                // 在缓存中查询 key
                int get(int key);
                // 将 key 和 val 存入缓存
                void put(int key, int val);
            }
            static class LFUCache implements ILFUCache{
                private HashMap<Integer,Integer> contentMap = new HashMap<>();
                private HashMap<Integer, LinkedHashSet<Integer>> freqMap = new HashMap<>();
                private int capacity;

                public LFUCache(int capcity) {
                    this.capacity = capcity;
                }

                @Override
                public int get(int key) {
                    //修改key的被访问次数
                    if (contentMap.containsKey(key)){
                        Iterator<Entry<Integer, LinkedHashSet<Integer>>> iterator = freqMap.entrySet().iterator();
                        while (iterator.hasNext()){
                            Entry<Integer, LinkedHashSet<Integer>> currEntry = iterator.next();
                            LinkedHashSet<Integer> scannedFreqSet = currEntry.getValue();
                            if (! scannedFreqSet.contains(key)){
                                continue;
                            }
                            //删除并挪到下一级，下一个频度可能不存在
                            scannedFreqSet.remove(key);
                            LinkedHashSet<Integer> nextFreqSet = freqMap.computeIfAbsent(currEntry.getKey() + 1, LinkedHashSet::new);
                            nextFreqSet.add(key);
                            break;
                        }
                    }
                    return contentMap.getOrDefault(key, -1);
                }

                @Override
                public void put(int key, int val) {
                    if (contentMap.size() >= capacity && !contentMap.containsKey(key)){
                        Iterator<Entry<Integer, LinkedHashSet<Integer>>> freqMapIterator = freqMap.entrySet().iterator();
                        LinkedHashSet<Integer> removedFreqSet = freqMapIterator.next().getValue();
                        while (removedFreqSet.size() == 0){
                            removedFreqSet = freqMapIterator.next().getValue();
                        }
                        Integer removedKey = removedFreqSet.iterator().next();
                        removedFreqSet.remove(removedKey);
                        contentMap.remove(removedKey);
                    }
                    if (!contentMap.containsKey(key)){
                        LinkedHashSet<Integer> firstFreqSet = freqMap.computeIfAbsent(1, LinkedHashSet::new);
                        firstFreqSet.add(key);
                    }
                    contentMap.put(key,val);
                }
            }

            public static void main(String[] args) {
                LFUCache cache = new LFUCache(5);
                cache.put(1,2);
                cache.put(2,3);
                cache.put(3,4);
                cache.put(4,5);
                cache.get(1);
                cache.get(1);
                cache.get(3);
                cache.get(2);
                cache.put(5,6);
                cache.put(6,7);
                cache.put(7,6);
                cache.put(8,7);
                cache.put(2,6);
                cache.get(2);
                cache.get(7);
                cache.get(8);
                cache.get(2);
                cache.get(7);
                cache.get(8);
                cache.get(3);
                cache.get(10);
                cache.put(9,10);
                cache.put(10,11);
            }
        }

        static class TestHashMap{
            public static void main(String[] args) {
                Map<Integer,Integer> map = new HashMap<>();
                map.put(1,2);
                map.put(2,2);
                map.put(3,2);
                map.entrySet().iterator();
            }
        }
    }

    static class GanyuHighwayTerminalStreamTest{
        public static void main(String[] args) {
            List<Long> ids = Arrays.asList(12L, 13L, 14L,15L, 16L, 17L, 18L,19L,20L,21L,
                    22L,23L,24L,25L,26L,27L,28L,29L,30L,31L,
                    32L,33L,34L,35L,36L,37L,38L,39L,40L,41L);
            System.out.println(ids.size());
            int queryPerTime = 44;
            int times = ids.size() / queryPerTime + 1;
            List<Long>[] idsArrary = new List[times];
            for (int i = 0; i < times; i++) {
                int start = i * queryPerTime;
                int end = start+queryPerTime;
                end = end > ids.size()?ids.size():end;
                List<Long> longs = ids.subList(start, end);
                idsArrary[i] = longs;
            }
            Stream.of(idsArrary).map(item -> {
                System.out.println("去查询下属id: "+ item);
                return new ArrayList(){{add("bean1");add("bean2");}};
            });

        }
        static void helper(Spliterator<Long> idSpliterator){
            Spliterator<Long> half = null;
            if (idSpliterator.estimateSize() > 20){
                half = idSpliterator.trySplit();
                helper(half);
            }
            idSpliterator.forEachRemaining(item -> {
                System.out.println(item);
            });

        }
    }
}
