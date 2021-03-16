package com.longpc.supportportalspringboot;

import com.longpc.supportportalspringboot.constant.FileConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

@SpringBootApplication
public class SupportPortalSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportPortalSpringbootApplication.class, args);
        new File(FileConstant.USER_FOLDER).mkdirs();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
