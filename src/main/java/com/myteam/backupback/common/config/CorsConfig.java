package com.myteam.backupback.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 允许来自任何域的跨域请求
        corsConfiguration.addAllowedHeader("*"); // 允许任何类型的请求头
        corsConfiguration.addAllowedMethod("*"); // 允许所有 HTTP 请求方法
        source.registerCorsConfiguration("/**", corsConfiguration); // 将配置应用到所有的接口
        return new CorsFilter(source);
    }
}