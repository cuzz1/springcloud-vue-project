package com.leyou.order.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 98050
 * @create: 2018-10-27 11:38
 **/
@ConfigurationProperties(prefix = "leyou.pay")
public class PayProperties {

    /**
     * 公众账号ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 生成签名的密钥
     */
    private String key;

    /**
     * 连接超时时间
     */
    private int connectTimeoutMs;

    /**
     * 读取超时时间
     */
    private int readTimeoutMs;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }
}
