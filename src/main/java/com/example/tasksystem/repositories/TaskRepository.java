package com.example.tasksystem.repositories;

import com.example.tasksystem.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для доступа к задачам в базе данных.
 * Расширяет интерфейс JpaRepository, предоставляющий стандартные методы для работы с базой данных.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
