package ru.example.operator.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("ru.example")
@EnableScheduling
public class GatewayK8sOperatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayK8sOperatorApplication.class, args);
    }
}
