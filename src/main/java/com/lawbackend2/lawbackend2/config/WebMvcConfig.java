package com.lawbackend2.lawbackend2.config;

import com.lawbackend2.lawbackend2.interceptor.UserActivityInterceptor;
import com.lawbackend2.lawbackend2.license.interceptor.LicenseCheckInterceptor;
import com.lawbackend2.lawbackend2.resolver.CurrentUserIdArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LicenseCheckInterceptor licenseCheckInterceptor;

    @Autowired
    private UserActivityInterceptor userActivityInterceptor;

    // 显式配置MultipartResolver，确保文件上传请求能够正确解析
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserIdArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userActivityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/error"
                );

        registry.addInterceptor(licenseCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/system/license/**",
                        "/api/v1/system/license/**",
                        "/auth/**",
                        "/api/v1/auth/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/error"
                );
    }

    // 此配置确保SockJS能够正确处理静态资源路径
    // 当应用程序有context-path时，SockJS需要这个配置来定位iframe.html等资源
}
