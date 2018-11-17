package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: cuzz
 * @Date: 2018/11/17 14:17
 * @Description:
 */
public interface UserApi {

    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    User queryUser(@RequestParam("username") String username, @RequestParam("password") String password);

}
