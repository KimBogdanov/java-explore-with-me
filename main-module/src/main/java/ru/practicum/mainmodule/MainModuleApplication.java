package ru.practicum.mainmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ru.practicum.statisticservice")
@ComponentScan("ru.practicum.mainmodule")
public class MainModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainModuleApplication.class, args);
    }
}
