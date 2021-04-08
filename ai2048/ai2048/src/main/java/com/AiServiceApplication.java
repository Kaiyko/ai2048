package com;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Description：
 *
 * @author 1998
 * @date 2020/11/24 15:54
 */

@SpringBootApplication
@EnableWebMvc
@ComponentScan(basePackages = {"com.kaykio.*"})
public class AiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
    }
}
