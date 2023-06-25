package com.example.tasksystem.components;

import com.example.tasksystem.models.Task;
import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.TaskRepository;
import com.example.tasksystem.repositories.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Компонент, периодически вычисляющий сводку и предоставляющий её по запросу.
 */
@Component
@Getter
public class Summariser {

    /**
     * Репозиторий для доступа к задачам в базе данных.
     */
    @Autowired
    TaskRepository taskRepository;

    /**
     * Репозиторий для доступа к пользователям в базе данных.
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Время последнего обновления сводки.
     */
    private String updated;

    /**
     * Количество пользователей.
     */
    private int numUsers;

    /**
     * Количество пользователей без задач.
     */
    private int numFreeUsers;

    /**
     * Количество задач.
     */
    private int numTasks;

    /**
     * Количество задач с прошедшим дедлайном.
     */
    private int numExpired;

    /**
     * Количество пользователей, пропустивших дедлайн.
     */
    private int numMissedDeadline;

    /**
     * Количество задач, отмеченных пользователями как завершённые.
     */
    private int numReview;

    /**
     * Функция, вычисляющая сводку.
     * Аннотация @Scheduled производит выполнение функции каждые 20 секунд.
     */
    @Scheduled(cron = "0/20 * * * * *")
    public void update() {
        numUsers = 0;
        numFreeUsers = 0;
        numTasks = 0;
        numExpired = 0;
        numMissedDeadline = 0;
        numReview = 0;
        for (User user : userRepository.findAll()) {
            numUsers += 1;
            boolean missed = false;
            List<Task> tasks = taskRepository.findByUser(user);
            for (Task task : tasks) {
                task.updateStyle();
                taskRepository.save(task);
                numTasks += 1;
                if (task.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                    numExpired += 1;
                    missed = true;
                }
                if (task.isCompleted()) {
                    numReview += 1;
                }
            }
            if (tasks.size() == 0) {
                numFreeUsers += 1;
            }
            if (missed) {
                numMissedDeadline += 1;
            }
        }
        updated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
