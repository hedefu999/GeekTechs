package com;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class ApacheVelocity {
    public static class JSONParser {
        public static JSONObject parseJSONObj(String resp){
            try {
                return JSON.parseObject(resp);
            }catch (Exception e){
                return new JSONObject();
            }
        }

        public static void removeKeys(JSONObject data, String... keys){
            if (keys == null || keys.length == 0){
                return;
            }
            for (String key : keys) {
                data.remove(key);
            }
        }

        public static String addKVPairs(JSONObject data,String... params){
            if (data == null){
                return StringUtils.EMPTY;
            }
            if (params == null || params.length == 0){
                return data.toJSONString();
            }
            for (int i = 0; i < params.length / 2; i++) {
                String key = params[2 * i];
                String value = params[2 * i + 1];
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
                    data.put(key, value);
                }
            }
            return data.toJSONString();
        }
    }
    // 单例velocity引擎
    static VelocityEngine ve = new VelocityEngine();
    static VelocityContext rootCtx = new VelocityContext();
    static {
        // 初始化velocity引擎
        Properties p = new Properties();
        p.put("resource.loader", "string");
        p.put("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        p.setProperty("string.resource.loader.repository.static", "false");
        p.setProperty("string.resource.loader.cache", "true"); // 开启模板缓存
        p.setProperty("string.resource.loader.modificationCheckInterval", "5"); // 5s刷新模板检查
        ve.init(p);
        try {
            // 装载常用工具类
            rootCtx.put("JSONParser", new JSONParser());
            rootCtx.put("JSONUtils", JSON.class);//Class.forName("com.alibaba.fastjson.JSON")
            rootCtx.put("String", String.class);
            rootCtx.put("Integer",Integer.class);
            rootCtx.put("StringUtils", StringUtils.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //http请求加密时经常出现顺序问题，多用用LinkedHashMap
    static String process(String velocityTemplate, Map<String, String> paramContext){
        String templateId = "1231";
        StringResourceRepository repo = (StringResourceRepository) ve.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
        repo.putStringResource(templateId, velocityTemplate);

        Template t = ve.getTemplate(templateId);

        // 约定在适配模板中使用$resp代表原始response
        VelocityContext vc = new VelocityContext(rootCtx);

        paramContext.entrySet().stream().forEach(entry -> {
            vc.put(entry.getKey(), entry.getValue());
        });

        StringWriter writer = new StringWriter();
        t.merge(vc, writer);
        return writer.toString();
    }

    /**
    $cutType 不存在时会原样显示，当希望展示成空白时，可以$!cutType
     但若还要默认值，则可以写额外脚本
     #if ( !$cutType || $!cutType == "")
     #set ( $cutType = "1" )
     #end

     velocity/daily_test.vm 文件中的内容

     #set($respJO=$JSONParser.parseJSONObj($resp))
     #set($respHead=$respJO.respHead)
     #set($respcd=$respHead.resp_cd)
     #set($respMsg=$respHead.rqst_id+$respHead.resp_msg)
     #if("$!respcd"!="" && $respcd=="000000")
     #set($respDecrypt=$PuDaoCreditGMUtils.parseRespBody($respHead.comprs,$respJO.respBody))
     #set($flag=$respDecrypt.flag)
     #set($oriResp=$respDecrypt.data)
     #if("$!flag"!="" && $flag=="1")
     #set($pdResp={"success":true,"effective":true,"code":$respcd,"message":$respMsg,"data":$oriResp})
     #else
     #set($pdResp={"success":true,"effective":false,"code":$respcd,"message":$respMsg,"data":$oriResp})
     #end
     #else
     #set($pdResp={"success":true,"effective":false,"code":$respcd,"message":$respMsg})
     #end
     $JSONUtils.toJSONString($pdResp)

     PuDaoCreditGMUtils 此方法不提供，自行替换
     */
    static void dailyTest() throws Exception{
        //原始的成功加密响应
        String applydayResp = "{\"respBody\":\"MIIHzAYKKoEcz1UGAQQCA6CCB7wwgge4AgEAMYHTMIHQAgEAMEIwLjESMBAGA1UEAUrSyu3v4XjA22vwXhcQvffV7EPmAVjQG7BXtEgk=\",\"respHead\":{\"resp_cd\":\"000000\",\"resp_msg\":\"处理成功\",\"rqst_id\":\"20230427172257350781\",\"comprs\":\"1\"}}";
        //失败响应
        String applydayFailResp = "{\"respBody\":\"\",\"respHead\":{\"resp_cd\":\"000024\",\"resp_msg\":\"请求报文签名未通过验证\",\"rqst_id\":\"20230427194923000000\",\"comprs\":\"0\"}}";
        //成功解密的响应
        String applydayDecryptedResp = "{\"respBody\":\"{\\\"flag\\\":\\\"1\\\",\\\"data\\\":{\\\"result\\\":0,\\\"data\\\":{\\\"ft_tag_parent_k7tok12\\\":\\\"0\\\",\\\"ft_tag_parent\\\":\\\"0\\\"}}}\",\"respHead\":{\"resp_cd\":\"000000\",\"resp_msg\":\"处理成功\",\"rqst_id\":\"20220822183413000022\",\"comprs\":\"0\"}}";
        String path = Thread.currentThread().getContextClassLoader().getResource("velocity/daily_test.vm").getPath();
        String velocityTemplate = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("resp",applydayResp);
        String process = process(velocityTemplate, paramMap);
        System.out.println(process);
    }


}
