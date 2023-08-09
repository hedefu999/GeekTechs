package com.amazons3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

public class S3CompatiblePrimary {
    public static final String endpointAddr = "10.189.108.149";
    public static final String accessKey = "AKIAIOSFODNN7EXAMPLE";
    public static final String secretKey = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    public static final String BUCKET_FRE_ADMIN = "fre-admin";

    //下面两个对象应使用单例的，否则可能产生异常（https://github.com/aws/aws-sdk-java/issues/1463）
    private static AmazonS3 s3Client;
    private static TransferManager transferManager;
    static {
        //账户信息
        AWSCredentialsProvider provider =
                new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        //接入点信息
        AwsClientBuilder.EndpointConfiguration endpoint =
                new AwsClientBuilder.EndpointConfiguration(endpointAddr, "VOS");
        //客户端配置，需要时可对诸如连接超时等进行设置。此处使用了 v2 的签名方式，v4 的签名方式会包含 host，某些情景如 presigned url 情况下会有影响。
        ClientConfiguration clientConfiguration =
                new ClientConfiguration().withProtocol(Protocol.HTTP).withSignerOverride("S3SignerType");
        //创建客户端，withPathStyleAccessEnabled 强制使用 bucket/key 的 URL 格式，（AWS 默认是将 bucket 加到 host 前面）
        s3Client = AmazonS3Client.builder()
                .withClientConfiguration(clientConfiguration)
                .withCredentials(provider)
                .withEndpointConfiguration(endpoint)
                .withPathStyleAccessEnabled(true)
                .build();
        transferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build();
    }

    static void checkFileExists(String key){
        boolean fileExist = s3Client.doesObjectExist(BUCKET_FRE_ADMIN, key);
        System.out.println(fileExist);
        String data="{\"result\":{\"channelAccountDtoList\":[{\"loanProviderCode\":\"030120\",\"channelUid\":\"2864552906346988790166\",\"partnerUid\":\"6393576983705481667\",\"channelType\":2,\"customerLabel\":\"01\",\"vUserId\":\"258500166\",\"assignChannelFlag\":0,\"majorUid\":\"2342949387145052670166\",\"attributes\":\"{\\\"cooprContNo\\\":\\\"BC01202109220593\\\",\\\"loanCorp\\\":\\\"ZYCFC\\\",\\\"loanType\\\":\\\"PDI0512\\\",\\\"mainAcctNo\\\":\\\"CTI582021102348254270\\\",\\\"openId\\\":\\\"6393576983705481667\\\",\\\"outerOrderNo\\\":\\\"582021102305p74j6mgpq\\\"}\",\"creditTime\":\"2021-10-23 05:37:33\",\"multiCreditFlag\":1,\"loanCorp\":\"ZYCFC\",\"status\":1},{\"loanProviderCode\":\"030115\",\"channelUid\":\"2342949624133718270166\",\"partnerUid\":\"1874359510630404664010166F02\",\"channelType\":2,\"statusTypeList\":[1,3],\"customerLabel\":\"\",\"vUserId\":\"258500166\",\"assignChannelFlag\":0,\"majorUid\":\"2342949387145052670166\",\"attributes\":\"\",\"creditTime\":\"2020-03-26 11:53:28\",\"multiCreditFlag\":1,\"loanCorp\":\"JSB\",\"status\":3},{\"loanProviderCode\":\"030107\",\"channelUid\":\"2342949387207967230166\",\"partnerUid\":\"1874357536623170585010166F01\",\"channelType\":2,\"statusTypeList\":[3],\"customerLabel\":\"\",\"vUserId\":\"258500166\",\"assignChannelFlag\":0,\"majorUid\":\"2342949387145052670166\",\"attributes\":\"\",\"creditTime\":\"2020-03-26 11:50:55\",\"multiCreditFlag\":1,\"loanCorp\":\"NJCB\",\"status\":3}],\"commonResp\":{\"success\":1,\"resultCode\":\"200\",\"message\":\"服务调用成功\"}},\"returnCode\":\"0\"}";
    }

    static void deleteObject(String key){
        DeleteObjectRequest request = new DeleteObjectRequest(BUCKET_FRE_ADMIN, key);
        s3Client.deleteObject(request);
    }

    public static void main(String[] args) {
        String key = "a24cc60eb1834a3dafe4210b8ea5a085.xsd";
        deleteObject(key);
        checkFileExists(key);
    }
}
