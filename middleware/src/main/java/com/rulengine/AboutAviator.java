package com.rulengine;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 轻量级 Java 表达式求值引擎 Google Aviator
 * 官方参考文档 https://code.google.com/archive/p/aviator/wikis/User_Guide_zh.wiki?spm=a2c4e.10696291.0.0.682219a4bhEzuL&file=User_Guide_zh.wiki
 * 相比于 Groovy Drools 非常轻量和高性能
 * Aviator 的实现思路与其他求值器存在不同，其他求值器一般通过解释的方式执行，而 Aviator 是直接将表达式编译成 Java 字节码，交给 JVM 去执行
 * Aviator 的定位 介于 Groovy 重量级脚本语言和 IKExpression 这样的轻量级表达式引擎之间
 * Aviator 的结构：lexer -> Parser -> CodeGenerator -> JVM
 */
public class AboutAviator {
private static final Logger logger = LoggerFactory.getLogger(AboutAviator.class);

static class JuniorStage{
@Test
public void basicAPI(){
    //Integer intResult = (Integer) AviatorEvaluator.execute("1+2+3");
    // 上述写法报错 java.lang.Long cannot be cast to java.lang.Integer
    Long longResult = (Long) AviatorEvaluator.execute("1+2+3");

    //传递变量进来
    Map<String,Object> env = new HashMap<>();
    env.put("a", 10);
    Boolean boolTestResult = (Boolean) AviatorEvaluator.execute("a > 5", env);
    //a 这个变量不传，上面的表达式也能得到 false，如何进行空判断？
    boolTestResult = (Boolean) AviatorEvaluator.execute("a != nil", env);
    System.out.println(boolTestResult);

    /**
     Aviator数值类型只支持 Long 和 Double，不论用户传入的变量还是返回的结果，任何整数都会被转换成 Long，任何浮点数默认都会被转换成 Double
     金融计算需要精度更高的 BigDecimal，可以通过 setOption设置
     */
    // 解析浮点数为 Decimal 类型
    AviatorEvaluator.getInstance().setOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL, true);
    // 解析整数为 Decimal 类型
    AviatorEvaluator.getInstance().setOption(Options.ALWAYS_PARSE_INTEGRAL_NUMBER_INTO_DECIMAL, true);
    //设置了整数和浮点数都是 BigDecimal 类型后，用户输入的变量必须为 BigDecimal 类型，脚本执行结果返回的数字也是 BigDecimal 类型
    //ClassCastException: java.math.BigDecimal cannot be cast to java.lang.Long
    //decimalResult = (Long) AviatorEvaluator.execute("1+2+3");

    Boolean boolResult = (Boolean) AviatorEvaluator.execute("2>3||2!=4");
    /**
     传递变量时推荐使用 Expression#newEnv GoogleAviator 提供的 SymbolHashMap性能要比 HashMap 强
     */
    Expression expression = AviatorEvaluator.getInstance().compile("dailyTestScript01",
            "'hello everyone, I am ' + name + ', and I am ' + age + ' years old'", true);
    String helloResult = (String) expression.execute(expression.newEnv("name", "jack", "age", 12));

    //使用 Aviator 内置函数
    AviatorEvaluator.getInstance().setOption(Options.ALWAYS_PARSE_INTEGRAL_NUMBER_INTO_DECIMAL, false);
    env.put("name", "Dalabengba Banda bedib dobiroon");
    Long lengthResult = (Long) AviatorEvaluator.execute("string.length(name)",env);

    /** Aviator脚本可以放在单独的脚本文件中，文件扩展名 .av
     多行表达式脚本，每行以分号 ; 结尾，最后 return 一个结果，示例：aviator_test.av
     let a=1;
     let b=2;
     c = a + b;
     return c;
     AviatorScript中的每一行表达式都有一个返回值，加上分号就表示丢弃这个值返回 nil（除了 return）
     当不写 return 语句时，最后一行表达式需要去掉分号 ; 才能返回值
     */
    try {
        Expression exp = AviatorEvaluator.getInstance().compileScript("aviator_test.av");
        Object result = exp.execute();
    } catch (IOException e) { }

    /**
     * Aviator支持嵌套变量，类似 jsonPath的方式获取属性
     */
    Person person = new Person();
    person.setName("jack");
    env.put("prs",person);
    String prsName = (String) AviatorEvaluator.execute("prs.name", env);
    System.out.println(prsName);
}
@Data
static class Person{
    private String name;
}
/**
 下面提供一些使用 Aviator内置函数的脚本,其他参考文档
 string.length(name)
 string.contains(srcStr, unitStr)
 math.pow(-3,2)
 math.sqrt(9.0)
 sysdate() 返回 java.util.Date()
 now() = System.currentTimeMillis()
 rand() 0-1的随机数
 str() 转化为 string
 date_to_string(date,format)、string_to_date(source,format)
 map(collection,func) 即 stream().map(func::apply).collect(Collectors.toList())
 filter(collection,func) 即 stream().filter(func::apply).collect(Collectors.toList())
 int count(collection)
 boolean include(collection,element) 即 collection.contains(element)
 sort(collection)
 filter(collection, collection.neq(value)) 收集一个集合collection中值不为 value 的元素，除了 neq，还有 eq、gt、ge、le、nil、exist
 */

/**
 * Aviator支持 UDF，可以玩出更多花样
 * 两个步骤：继承 AbstractFunction、注册到 AviatorEvaluator
 */
static class CustomizedFunction{
    static class HttpInvokeUDF extends AbstractFunction{
        private final Logger logger = LoggerFactory.getLogger(HttpInvokeUDF.class);
        @Override
        public String getName() {
            return "notify";
        }
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            Number receiver = FunctionUtils.getNumberValue(arg1, env);
            String message = FunctionUtils.getStringValue(arg2, env);
            logger.info("模拟 HTTP 请求：向工号为 {} 的人员发出通知：{}", receiver.longValue(), message);
            return new AviatorString("SUCCESS");
        }
    }
    @Test
    public void testUDF(){
        AviatorEvaluator.addFunction(new HttpInvokeUDF());
        Map<String, Object> env = new HashMap<>();
        env.put("workNo", 32767);
        env.put("msg", "您的审批已同意，请签收");
        env.put("applyStatus", 1);
        String udfResult = (String)AviatorEvaluator.execute("if(applyStatus == 1){notify(workNo,msg);}", env);
        System.out.println(udfResult);
    }
}

/** 性能优化
 缓存管理、EnvHashMap传递变量
 */
static void manageCache(){
            //默认使用 脚本本身 作为 key，脚本较长时推荐使用 MD5串 作为 cacheKey
            String expressionCacheKey = "scriptMD5Value";
            String script = "1==1";
            AviatorEvaluator.getInstance().compile(expressionCacheKey, script, true);
            int expressionCacheSize = AviatorEvaluator.getInstance().getExpressionCacheSize();
            logger.info("缓存的表达式大小：{}", expressionCacheSize);
            //直接清空缓存
            AviatorEvaluator.clearExpressionCache();
            Expression cachedExpression = AviatorEvaluator.getCachedExpression(expressionCacheKey);
            if (cachedExpression == null){
                logger.info("this expression was not cached in advance");
            }
            //AviatorEvaluator.invalidateCache(script);
            AviatorEvaluator.getInstance().invalidateCacheByKey(expressionCacheKey);
        }
}
//一些企业级生产实践问题
static class MediumStage{
/**
 * double类型精度问题，使用 big decimal 解决
 * 也可以通过为数字添加后缀的方式提示数字的类型：N 表示 big int，如 1N、2N、3457743N; M 表示decimal，如 1M、2.54M;
 */
@Test
public void doubleAccuracyMissed(){
    double a = 0.1d;
    double b = 0.1d;
    System.out.println( a*b );//得到 0.010000000000000002
    Object result = AviatorEvaluator.execute("0.1*0.1");
    logger.info("结果类型：{}, 值：{}", result.getClass().getSimpleName(), result);//结果类型：Double, 值：0.010000000000000002
    //需要将Double 转换为 BigDecimal 类型来计算
    result = AviatorEvaluator.execute("0.1M*0.1M");
    logger.info("结果类型：{}, 值：{}", result.getClass().getSimpleName(), result);//结果类型：BigDecimal, 值：0.01
    //变量是无法声明成 BigDecimal 类型的，只能通过 AviatorEvaluatorInstance.setOption 设置所有数字按 big decimal 类型处理
}

}


}
