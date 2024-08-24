package com.spark.utils;

import com.spark.exception.LycheeSparkException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author LYCHEE
 * 请求工具类
 */
@Slf4j
public class HttpUtils {

    // 连接超时（毫秒）
    private static final int CONNECT_TIMEOUT = 5000;
    // 读取超时（毫秒）
    private static final int SOCKET_TIMEOUT = 10000;
    // 最大连接数
    private static final int MAX_TOTAL_CONNECTIONS = 200;
    // 每个路由的最大连接数
    private static final int MAX_CONNECTIONS_PER_ROUTE = 200;
    // 连接池中获取连接的超时时间
    private static final int CONNECTION_REQUEST_TIMEOUT = 500;

    private static final CloseableHttpClient HTTP_CLIENT;

    static {
        // 创建连接池管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        // 设置请求配置
        RequestConfig config = RequestConfig.custom()
                //连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectTimeout(CONNECT_TIMEOUT)
                //服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setSocketTimeout(SOCKET_TIMEOUT)
                //从连接池中获取连接的超时时间，超过该时间未拿到可用连接
                //会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .build();

        // 创建HttpClient
        HTTP_CLIENT = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(config)
                .build();
    }

    /**
     * 获取HttpClient实例
     * @return httpClient
     */
    public static CloseableHttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    /**
     * Get请求
     * @param url 地址
     * @return 结果
     */
    public static String get(String url){
        return get(url, null);
    }

    /**
     * Get请求
     * @param url 地址
     * @param params 参数
     * @return 结果
     */
    public static String get(String url, Map<String, String> params){
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            // 将参数添加到URL中
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            URI uri = uriBuilder.build();
            HttpGet request = new HttpGet(uri);
            CloseableHttpResponse response = HTTP_CLIENT.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                String format = DateUtils.format(LocalDate.now(), DateUtils.DATE_TIME_FORMAT);
                log.error("请求失败，时间：{}，请求地址：{}，状态码：{}",format,url,statusCode);
                throw new LycheeSparkException("请求失败");
            }
        } catch (Exception e) {
            log.error("请求异常",e);
            throw new LycheeSparkException("请求异常");
        }
    }


    /**
     * Post请求
     * @param url 地址
     * @return 结果
     */
    public static String post(String url){
        return post(url, null, null);
    }

    /**
     * Post请求
     * @param url 地址
     * @param paramsJson 参数
     * @return 结果
     */
    public static String post(String url, String paramsJson){
        return post(url, paramsJson, null);
    }

    /**
     * 发送POST请求图片资源
     * @param url 地址
     * @param paramsJson 参数
     * @return BufferedImage
     */
    public static BufferedImage postImg(String url, String paramsJson) throws IOException {
        CloseableHttpResponse response = post(url, paramsJson, null, "image/png");
        InputStream inputStream = null;
        // 检查响应是否包含图像
        if (response.getEntity() != null) {
            String contentType = response.getEntity().getContentType().getValue();
            if (contentType.startsWith("image/")){
                // 从响应实体获取输入流
                inputStream = response.getEntity().getContent();
            }else if (contentType.startsWith("application/json")){
                String resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                throw new LycheeSparkException(resultString);
            }else {
                log.error("请求二维码响应类型未知");
                throw new LycheeSparkException("请求二维码响应类型未知");
            }
        } else {
            log.error("请求二维码响应内容为空");
            throw new LycheeSparkException("请求二维码响应内容为空");
        }
        return ImageIO.read(inputStream);
    }

    /**
     * Post请求
     * @param url 地址
     * @param paramsJson 参数
     * @param headers 头部信息
     * @return 结果
     */
    public static String post(String url, String paramsJson, Map<String,String> headers){
        try {
            HttpPost httpPost = new HttpPost(url);
            if (headers != null){
                // 设置头部信息
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(),entry.getValue());
                }
            }else {
                httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            }
            if (StringUtils.isNotEmpty(paramsJson)){
                StringEntity entityParams = new StringEntity(paramsJson, StandardCharsets.UTF_8);
                entityParams.setContentType("application/json");
                httpPost.setEntity(entityParams);
            }
            CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                String format = DateUtils.format(LocalDate.now(), DateUtils.DATE_TIME_FORMAT);
                log.error("请求失败，时间：{}，请求地址：{}，状态码：{}",format,url,statusCode);
                throw new LycheeSparkException("请求失败");
            }
        } catch (Exception e) {
            log.error("请求异常",e);
            throw new LycheeSparkException("请求异常");
        }
    }

    /**
     * Post请求
     * @param url 地址
     * @param paramsJson 参数
     * @param headers 头部信息
     * @param requestBodyType 请求体类型
     * @return 结果
     */
    public static CloseableHttpResponse post(String url, String paramsJson, Map<String,String> headers, String requestBodyType){
        if (StringUtils.isEmpty(requestBodyType)){
            requestBodyType = "application/json; charset=utf-8";
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            if (headers != null){
                // 设置头部信息
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(),entry.getValue());
                }
            }else {
                httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            }
            if (StringUtils.isNotEmpty(paramsJson)){
                StringEntity entityParams = new StringEntity(paramsJson, StandardCharsets.UTF_8);
                entityParams.setContentType(requestBodyType);
                httpPost.setEntity(entityParams);
            }
            return HTTP_CLIENT.execute(httpPost);
        } catch (Exception e) {
            log.error("请求异常",e);
            throw new LycheeSparkException("请求异常");
        }
    }

    /**
     * 关闭HttpClient连接池
     */
    public static void close() throws IOException {
        if (HTTP_CLIENT != null) {
            HTTP_CLIENT.close();
        }
    }
}
