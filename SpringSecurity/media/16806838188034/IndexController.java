package com.example.day04;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 编程学习
 * @Date 2023/4/8
 * @Description
 */
@RestController
public class IndexController {
    @RequestMapping("/")
    public String index(Authentication authentication){
        return JSON.toJSONString(authentication);
    }
}
