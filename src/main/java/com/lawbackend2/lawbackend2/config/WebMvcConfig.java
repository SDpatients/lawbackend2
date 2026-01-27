package com.lawbackend2.lawbackend2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 此配置确保SockJS能够正确处理静态资源路径
    // 当应用程序有context-path时，SockJS需要这个配置来定位iframe.html等资源
}
