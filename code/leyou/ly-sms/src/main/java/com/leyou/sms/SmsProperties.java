package com.leyou.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: cuzz
 * @Date: 2018/11/16 14:21
 * @Description:
 */
@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {

    String accessKeyId;

    String accessKeySecret;

    String signName;

    String verifyCodeTemplate;

}
