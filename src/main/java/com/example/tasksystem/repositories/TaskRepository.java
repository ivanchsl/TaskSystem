package com.example.tasksystem.repositories;

import com.example.tasksystem.models.Task;
import com.example.tasksystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для доступа к задачам в базе данных.
 * Расширяет интерфейс JpaRepository, предоставляющий стандартные методы для работы с базой данных.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Поиск задач по имени пользователя.
     * @param user пользователь.
     * @return найденные задачи.
     */
    List<Task> findByUser(User user);

}
