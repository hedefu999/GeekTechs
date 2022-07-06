package flowlimiter;

import com.google.common.util.concurrent.RateLimiter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Throttle 限流
 * 限流方案有哪些
 * 限流算法有漏桶/令牌桶算法
 * 限流实现可以采用semaphore/guava ratelimiter,分布式环境下还可以使用redis,企业级环境就要用到阿里sentinel中间件
 */
public class ThrottlingStrategy {
    static ScheduledExecutorService fixRateThreadPool = Executors.newScheduledThreadPool(1);
    /**
     * guava的RateLimiter限流器
     */
    static class GuavaRateLimiterResearch{

        static RateLimiter rateLimiter = RateLimiter.create(2);
        //每秒发放5个令牌
        static RateLimiter rate5Limiter = RateLimiter.create(5);
        //带有3秒预热期的每秒发放5个令牌的平滑预热限流器
        static RateLimiter smoothWarmLimiter = RateLimiter.create(5,5,TimeUnit.SECONDS);
        static void knowBasicFeature(){
            while (true){
                double acquire = rateLimiter.acquire();
                //acquire差不多是0.5，表示ratelimiter发放令牌的速度是固定每秒2个
                System.out.println(acquire);
            }
        }
        static void basicFeature2(){
            while (true){
                System.out.println("get 5: " + rate5Limiter.acquire(70));
                System.out.println("get 1: " + rate5Limiter.acquire(2));
                System.out.println("get 1: " + rate5Limiter.acquire(1));
                System.out.println("get 1: " + rate5Limiter.acquire(1));
                System.out.println("-=-=-=- next stage -=--=-=");
            }
            /**
             get 3: 0.0
             get 1: 0.596662 //刚开始桶装满令牌，一次取完可以不用等。但后面再取就要等桶装满才能进行，就要等0.6秒
             get 1: 0.194171
             get 1: 0.199935
             -=-=-=- next stage -=--=-=
             get 3: 0.198628 //令牌桶少一个就满了，要等满了再操作，所以此时即便剩余4个令牌，取3个也要等0.2秒
             get 1: 0.595984
             get 1: 0.199457
             get 1: 0.195925
             总结：初始时令牌桶取令牌不用等，后面就要等桶满才能取令牌
                  令牌桶取令牌数量N超过桶容量C时，只会等一个令牌添加时间 1/C = 0.2 秒.随后会等N/C秒
                  其他情况建议写demo尝试，规则比较复杂

             规律：容量为C的令牌桶每次取令牌的耗时取决于上一次令牌（数量M） - M/C
             */
        }
        static void smoothWarmUpFeature(){
            while (true){
                System.out.println("got 1" + smoothWarmLimiter.acquire(1));
                System.out.println("got 1" + smoothWarmLimiter.acquire(1));
                System.out.println("got 1" + smoothWarmLimiter.acquire(1));
                System.out.println("-=-=-=- next stage -=-=-=-");
            }//5 秒预热期acquire的速度较慢，后面会进入正常的1秒5个令牌
            //acquire方法返回的数值应忽略
        }
        static void concurrentFeature(){
            //由于限流器的存在，周期定时200ms执行的任务也变成了500ms执行一次
            ScheduledFuture<?> scheduledFuture = fixRateThreadPool.scheduleAtFixedRate(() -> {
                System.out.println(LocalDateTime.now().getSecond() + " enter");
                double acquire = rateLimiter.acquire();
                System.out.println(LocalDateTime.now().getSecond() + " got: " + acquire);
            }, 1, 200, TimeUnit.MILLISECONDS);
        }
        public static void main(String[] args) {
            concurrentFeature();
        }
    }

    /**
     * Redis实现的滑动窗口限流器
     使用Zset实现，通常的思路是：
     - 依据具体业务场景确定Zset的key，如用户限制点击次数就可以使用userId作为key
     - add: 向这个key中添加元素，value任意，score是时间戳
     - removeRangeByScore: 同时将score - current > 滑动窗口时长的Zset元素移除掉（滑动窗口已从这些元素上移走）
     - cardinality: 此时才可以获取滑动窗口中的操作数
     - expire: 刷新此key过期时间,应不小于一个滑动窗口（针对冷用户）
     上述操作需要保证原子性，redis事务、pipeline、lua脚本均可
     */
    static class RedisZsetImplements{
        static JedisPool jedisPool = new JedisPool("localhost",6379);
        //滑动窗口内访问次数限制
        private static long maxCount = 5;
        //滑动窗口时长，单位 秒
        private static int slidingWindowWidth = 5;
        //增加一个key
        public void increment(String key){
            long current = System.currentTimeMillis();
            //--- 时间轴 ----->
            // latestToRemove | <- slidingWindow -> | future
            long latestToRemove = current - slidingWindowWidth * 1000;
            try (Jedis jedis = jedisPool.getResource()) {
                Transaction jedisTx = jedis.multi();
                jedisTx.zremrangeByScore(key, 0, latestToRemove);
                jedisTx.zadd(key, current, current + "- " + Math.random());
                jedisTx.expire(key, slidingWindowWidth);
                jedisTx.exec();
            }
        }
        //获取key
        public long getCount(String key){
            try (Jedis jedis = jedisPool.getResource()) {
                //jedisTx.zremrangeByScore(key,0,latestToRemove);
                return jedis.zcard(key);//获取元素数量，zcard - cardinality 基数
            }
        }
        //获取key的剩余有效时间
        public long getLeftTime(String key){
            try(Jedis jedis = jedisPool.getResource()) {
                return jedis.ttl(key);
            }
        }
        public boolean requestPassed(String key){
            long count = getCount(key);
            if (count < maxCount){
                increment(key);
                return true;
            }else {
                System.out.println("您被限流了，您已访问"+ count +"次，key失效还有"+ getLeftTime(key)+"秒");
                return false;
            }
        }

        /**
         *
         * @param bizCode 业务编码
         * @param userId 关联业务ID
         * @return 请求能否放行
         */
        static boolean limitBySlidingWindow(String bizCode, Long userId){
            String key = bizCode + ":" + userId;
            long current = System.currentTimeMillis();
            //Jedis extends BinaryJedis, 而BinaryJedis implements java.io.Closeable, 这就可以简化try-finally的写法。
            //另外实现java.lang.AutoCloseable接口也可以达到相同效果
            try (Jedis jedis = jedisPool.getResource()) {
                Pipeline pipeline = jedis.pipelined();
                Response<Long> countResp = pipeline.zcard(key);
                Long count = 0L;
                if (countResp != null && (count = countResp.get()) >= maxCount){
                    System.out.println("已访问了" + count + "次，被限流了");
                    return false;
                }

                pipeline.multi();
                pipeline.zadd(key, current, key + "_" + 100 * Math.random());
                pipeline.zremrangeByScore(key, 0, current - slidingWindowWidth * 1000);
                pipeline.expire(key, slidingWindowWidth + 1);
                pipeline.exec();

                pipeline.close();
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        public static void main(String[] args) {
            fixRateThreadPool.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    limitBySlidingWindow("supersale", 223L);
                }
            },0,400,TimeUnit.MILLISECONDS);
        }

    }
}
