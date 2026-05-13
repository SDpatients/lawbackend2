package com.lawbackend2.lawbackend2.security;

import com.lawbackend2.lawbackend2.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private AppProperties appProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .headers()
                .frameOptions().sameOrigin()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/system/license/status").permitAll()
                .antMatchers("/api/v1/system/license/status").permitAll()
                .antMatchers("/system/license/machine-code").permitAll()
                .antMatchers("/api/v1/system/license/machine-code").permitAll()
                .antMatchers("/system/license/upload").permitAll()
                .antMatchers("/api/v1/system/license/upload").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/sockjs/**").permitAll()
                .antMatchers("/api/v1/ws/**").permitAll()
                .antMatchers("/api/v1/sockjs/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/work-team/list/details").permitAll()
                .antMatchers("/api/v1/work-team/list/details").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated();

        // MultipartFilter必须在JwtAuthenticationFilter之前执行，以确保multipart请求被正确解析
        http.addFilterBefore(multipartFilter(), UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter应该在UsernamePasswordAuthenticationFilter之前执行
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        AppProperties.CorsConfig corsConfig = appProperties.getCors();
        Set<String> allowedOrigins = parseOriginList(corsConfig.getAllowedOrigins());

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOrigins.isEmpty()
                ? Collections.singletonList("*") 
                : new java.util.ArrayList<>(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(corsConfig.getAllowedMethods().split(",")));
        configuration.setAllowedHeaders(Arrays.asList(corsConfig.getAllowedHeaders().split(",")));
        configuration.setAllowCredentials(corsConfig.isAllowCredentials());
        configuration.setMaxAge(corsConfig.getMaxAge());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private Set<String> parseOriginList(String allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.trim().isEmpty() || "*".equals(allowedOrigins.trim())) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(allowedOrigins.trim().split("\\s*,\\s*")));
    }

    @Bean
    public MultipartFilter multipartFilter() {
        MultipartFilter multipartFilter = new MultipartFilter();
        multipartFilter.setMultipartResolverBeanName("multipartResolver");
        return multipartFilter;
    }
}
