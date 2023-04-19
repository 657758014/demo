package com.example;

import com.example.annotation.EnableFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableFeignClient(basePackages = {"com.example"})
@EnableFeignClient
@SpringBootApplication
public class DemoFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoFeignApplication.class, args);
    }

}
