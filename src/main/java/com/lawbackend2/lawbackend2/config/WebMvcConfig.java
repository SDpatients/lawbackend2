package com.lawbackend2.lawbackend2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 显式配置MultipartResolver，确保文件上传请求能够正确解析
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    // 此配置确保SockJS能够正确处理静态资源路径
    // 当应用程序有context-path时，SockJS需要这个配置来定位iframe.html等资源
}
