package com.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service("redpacketUserService")
public class RedpacketUserServiceImpl{
    private final Logger log = LoggerFactory.getLogger(RedpacketUserServiceImpl.class);
    private final String luaScriptFilePath = "lua/grab_red_packet.lua";

    /*-=-=-=-=-=- 使用redis服务 =-=-=-=-=-*/
    /**
     * 此处实现的redis抢红包逻辑
     * 全过程只有一次使用数据库操作，并且还是在新线程中操作，可以不影响主体流程的响应速度
     */
    @Autowired
    private RedisTemplate redisTemplate;
    private String luaScript;
    private static String luaScriptSHA;
    //private AtomicReference<String> luaScriptSHAAtomic = new AtomicReference<>();原子跟排他锁是两个场景下的策略

    /**
     * 红包的扣减和记录的逻辑是在redis中进行的
     */
    public long grabRedpacketByRedis(Integer redpacketId, Integer userId) {
        luaScript = FileUtils.readClassPathFileToString(luaScriptFilePath);
        //设置redis中链表存储的内容是args，以连字符连接的userId和时间戳
        String args = userId+"-"+System.currentTimeMillis();
        Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        //存在并发问题，这里会多次打印，加锁就好了
        synchronized (this){
            if (StringUtils.isEmpty(luaScriptSHA)){
                luaScriptSHA = jedis.scriptLoad(luaScript);
                log.info("加载lua脚本拿到SHA = {}", luaScriptSHA);
            }
        }
        Long result = (Long) jedis.evalsha(luaScriptSHA, 1, redpacketId + "", args);
        if (result == 2){//返回2表示抢到的是最后一个红包，此时应将redis中的数据持久化
            log.info("红包抢完发生在{}",System.currentTimeMillis());
            String unitAmountStr = jedis.hget("red_packet_"+redpacketId,"unit_amount");
            BigDecimal unitAmount = new BigDecimal(unitAmountStr);
            log.info("当前线程名称：{}", Thread.currentThread().getName());
            long start = System.currentTimeMillis();
            /**
             * 新开线程保存数据，但此处写成了同步等待的方式，返回为空可以更节约时间
             */
            Future<Integer> asyncResult = null;
            Integer saveCount = 0;
            try {
                saveCount = asyncResult.get(4, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.info("获取异步结果发生错误");
            }
            log.info("异步返回结果: {}, 耗时: {}", saveCount, System.currentTimeMillis()-start);
        }
        if (jedis != null && jedis.isConnected()){
            jedis.close();
        }
        return result;
    }
}
