package com.example.cleonoraadmin.config;

import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public Faker faker() {
        return new Faker();
    }


    @Value("${upload.folder.path}")
    private String projectPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:static/");





        registry.addResourceHandler("/" + Paths.get(
                                projectPath)
                        .subpath(
                                Paths.get(projectPath).getNameCount()-1,
                                Paths.get(projectPath).getNameCount()) +
                        "/**")
                .addResourceLocations("file:" +  projectPath + "/");
    }


}