package com.ambow.springboot_adoption.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration//xml文件
public class RestTemplateConfig {

    @Bean//相当于<Bean>
    public RestTemplate restTemplate() {
        // 创建请求工厂，可设置超时时间
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(40000); // 连接超时（毫秒）
        factory.setReadTimeout(40000);    // 读取超时（毫秒）

        // 创建RestTemplate并设置请求工厂
        return new RestTemplate(factory);
    }
}
