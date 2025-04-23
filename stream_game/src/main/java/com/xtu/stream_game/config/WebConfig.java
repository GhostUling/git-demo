package com.xtu.stream_game.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 配置CORS跨域支持
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
    
    /**
     * 配置静态资源访问
     * 将前端资源映射到标准目录结构
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 外部前端资源目录映射
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        // 自定义外部资源映射，方便开发阶段调试
        registry.addResourceHandler("/external/**")
                .addResourceLocations("file:./前端/");
    }
} 