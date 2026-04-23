package com.aiProject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aiProject.mapper")
public class AIProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIProjectApplication.class, args);
    }

}
