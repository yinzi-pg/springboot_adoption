package com.ambow.springboot_adoption.config;

import com.ambow.springboot_adoption.interceptor.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

       registry.addInterceptor(NoLoginInterceptor())
               .addPathPatterns("/**")
               .excludePathPatterns("/user/login","/user/register");
    }
    @Bean
    public NoLoginInterceptor NoLoginInterceptor() {
        return new NoLoginInterceptor();
    }


}
