package com.xtu.stream_game.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI streamGameOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stream游戏商城API文档")
                        .description("Stream游戏商城平台的RESTful API接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .url("http://www.example.com")
                                .email("contact@example.com"))
                        .license(new License().name("MIT License")));
    }
} 