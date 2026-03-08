package com.kemali;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan //开启对Servlet组件的支持
@SpringBootApplication
//@ComponentScan({"com.kemali","com.example"})
public class WebAiManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAiManagementApplication.class, args);
    }

}
