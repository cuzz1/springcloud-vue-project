package com.leyou.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.leyou.sms.SmsProperties;
import com.leyou.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: cuzz
 * @Date: 2018/11/16 15:39
 * @Description:
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties prop;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.sms.queue", durable = "true"),
            exchange = @Exchange(value = "ly.sms.exchange",
                    ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}))
    public void listenSms(Map<String, String> msg) throws Exception {
        if (msg == null || msg.size() <= 0) {
            // 放弃处理
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");

        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            // 放弃处理
            return;
        }
        // TODO 发送消息
        // SendSmsResponse resp = this.smsUtils.sendSms(phone, code,
        //         prop.getSignName(),
        //         prop.getVerifyCodeTemplate());
        // 发送失败
        // throw new RuntimeException();

        log.info("{消息服务} 正在给手机号为{}发送短信，code: {}", phone, code);

    }
}
