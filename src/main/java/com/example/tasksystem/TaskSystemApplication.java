package com.example.tasksystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Запуск приложения.
 * Аннотация @SpringBootApplication указывает, что это класс конфигурации Spring Boot.
 * Аннотация @EnableScheduling позволяет запускать отложенные задачи.
 * Метод main() запускает приложение, используя метод SpringApplication.run().
 */
@SpringBootApplication
@EnableScheduling
public class TaskSystemApplication {

    /**
     * Метод main() для запуска приложения.
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskSystemApplication.class, args);
    }

}
