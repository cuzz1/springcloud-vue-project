package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: cuzz
 * @Date: 2018/11/17 14:16
 * @Description:
 */
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}
