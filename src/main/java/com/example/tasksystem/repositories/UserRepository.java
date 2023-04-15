package com.example.tasksystem.repositories;

import com.example.tasksystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для доступа к пользователям в базе данных.
 * Расширяет интерфейс JpaRepository, предоставляющий стандартные методы для работы с базой данных.
 * Добавляет метод findByUsername для поиска пользователя по имени пользователя.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по имени пользователя.
     * @param username имя пользователя.
     * @return найденный пользователь или null, если пользователь не найден.
     */
    User findByUsername(String username);

}
