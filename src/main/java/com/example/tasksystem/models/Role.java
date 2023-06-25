package com.example.tasksystem.models;

import org.springframework.security.core.GrantedAuthority;

/**
 * Перечисление ролей пользователей.
 * Реализует интерфейс GrantedAuthority, используемый в Spring Security для представления ролей.
 */
public enum Role implements GrantedAuthority {

    /**
     * Роль пользователя.
     */
    USER, ADMIN;

    /**
     * Возвращает название роли.
     * @return название роли.
     */
    @Override
    public String getAuthority() {
        return name();
    }
}
