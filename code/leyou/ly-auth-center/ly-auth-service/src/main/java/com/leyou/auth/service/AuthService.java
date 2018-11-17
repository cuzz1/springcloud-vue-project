package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.entity.UserInfo;
import com.leyou.user.pojo.User;
import com.leyou.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @Author: cuzz
 * @Date: 2018/11/17 13:54
 * @Description:
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private JwtProperties jwtProp;

    @Autowired
    private UserClient userClient;

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    public String authentication(String username, String password) {

        // 查询用户
        User user = this.userClient.queryUser(username, password);
        if (user == null) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_OR_PASSWORD);
        }
        try {
            // 生成token
            String token = JwtUtils.generateToken(
                    new UserInfo(user.getId(), user.getUsername()),
                    jwtProp.getPrivateKey(), jwtProp.getExpire());
            return token;
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.GENERATE_TOKEN_ERROR);
        }
    }
}
