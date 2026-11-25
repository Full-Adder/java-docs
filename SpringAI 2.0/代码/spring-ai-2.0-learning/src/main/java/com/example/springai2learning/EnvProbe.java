package com.example.springai2learning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvProbe {

    @Autowired
    private org.springframework.core.env.Environment env;

    @GetMapping("/env")
    public String env() {
        return "embedding.base-url = " + env.getProperty("spring.ai.openai.embedding.base-url") + "\n"
             + "chat.base-url       = " + env.getProperty("spring.ai.openai.base-url") + "\n"
             + "embedding.api-key   = " + (env.getProperty("spring.ai.openai.embedding.api-key") != null ? "已设置" : "未设置");
    }
}
