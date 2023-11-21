package com;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpProtocolResearch {
    private static final Logger logger = LoggerFactory.getLogger(HttpProtocolResearch.class);
    /**-=-=--=-=-=-=-=-=-=-=- 通用参数 -=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
    static final String HOST = "http://127.0.0.1";
    static final String URL = "/test/doSth";
    static Map<String,Object> COMMON_PARAMS_MAP = new HashMap<String,Object>(){{
        put("aaa","bbb");
    }};

    /**
     *
     */
    static class AbountApacheHttpClient{
        static RequestConfig requestCfg = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setRedirectsEnabled(true) //默认允许自动重定向
                .build();
        static final CloseableHttpClient defaultHttpClient = HttpClients.createDefault();

        static void parseHttpResponse(CloseableHttpResponse response) throws Exception{
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.info("最终响应结果：{}", content);
            }
        }
        static void doCreateHttpClientManyWays() throws Exception{
            // 创建一个跳过SSL证书检查的httpClient，以便进行https站点访问
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        }

        static void doGetWithParams() throws Exception{
            URIBuilder uriBuilder = new URIBuilder(HOST + URL);
            for (Map.Entry<String, Object> entry : COMMON_PARAMS_MAP.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setConfig(requestCfg);
            CloseableHttpResponse response = defaultHttpClient.execute(httpGet);
            parseHttpResponse(response);
            //关闭以释放资源
            response.close();
            defaultHttpClient.close();
        }
        static void doGetWithoutParams() throws Exception{
            HttpGet httpGet = new HttpGet(HOST + URL);
            httpGet.setConfig(requestCfg);
            CloseableHttpResponse response = defaultHttpClient.execute(httpGet);
            parseHttpResponse(response);
        }
        static void doPostWithFormdata() throws Exception{
            List<NameValuePair> paramsInPost = new ArrayList<>();
            for (Map.Entry<String, Object> entry : COMMON_PARAMS_MAP.entrySet()) {
                paramsInPost.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
            HttpPost httpPost = new HttpPost(HOST + URL);
            httpPost.setConfig(requestCfg);
            httpPost.setEntity(new UrlEncodedFormEntity(paramsInPost, StandardCharsets.UTF_8));

            CloseableHttpResponse response = defaultHttpClient.execute(httpPost);
            parseHttpResponse(response);

            //关闭以释放资源
            response.close();
            defaultHttpClient.close();
        }
        static void doPostWithJSONBody(String jsonData) throws Exception{
            HttpPost httpPost = new HttpPost(HOST + URL);
            httpPost.setConfig(requestCfg);
            httpPost.setHeader("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
            //ContentType contentType = ContentType.create(MimeTypeUtils.APPLICATION_JSON_VALUE, StandardCharsets.UTF_8);
            httpPost.setEntity(new StringEntity(jsonData, ContentType.APPLICATION_JSON));
            logger.info("request = {}", EntityUtils.toString(httpPost.getEntity()));

            CloseableHttpResponse response = defaultHttpClient.execute(httpPost);
            parseHttpResponse(response);
        }
    }

    /**
     * 参考资料
     * 通用教程  https://blog.csdn.net/qq_36022463/article/details/128223424
     */
    static class AboutRestTemplate{
        static RestTemplate restTemplate = new RestTemplate();

        static void autoAssembleGetRequest(){
            String url = "http://localhost:8081/test/forward";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("name", "jack");

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
            URI uri = uriComponentsBuilder.queryParams(params).build().encode().toUri();

            String response = restTemplate.getForObject(uri, String.class);
            System.out.println(response);
        }

        static void replaceHolderGetRequest(){
            String url = "http://localhost:8081/test/forward?name={name}";
            Map<String,Object> params = new HashMap<>();
            params.put("name","jack");
            String response = restTemplate.getForObject(url, String.class, params);
        }

        // https://stackoverflow.com/questions/32392634/spring-resttemplate-redirect-302
        //https://stackoverflow.com/questions/29418583/follow-302-redirect-using-spring-resttemplate
        static void follow302Redirect(){

        }

        public static void main(String[] args) {

        }
    }

    /**
     * 推荐资料
     * 使用okhttpclient发起https请求 https://fuyongde.github.io/2020/12/10/how-use-httpclient-over-https/
     */
    static class AbountOkHttpClient{

    }

}
