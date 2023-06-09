package com.example.tasksystem.models;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс, представляющий пользователя в системе.
 * Имеет поля для имени пользователя, пароля, набора ролей, а также связь с задачами, которые принадлежат пользователю.
 */
@Getter
@Setter
@Entity
public class User {

    /**
     * Идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    private String username;

    /**
     * Пароль пользователя.
     */
    private String password;

    /**
     * Время последнего изменения списка задач.
     */
    private LocalDateTime lastModified;

    /**
     * Набор ролей пользователя.
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    /**
     * Задачи, которые принадлежат пользователю.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    /**
     * Создает нового пользователя без параметров.
     */
    public User() {
    }

    /**
     * Создает нового пользователя с заданными именем и паролем.
     * Устанавливает набор ролей по умолчанию, содержащий только роль USER.
     * @param username имя пользователя.
     * @param password пароль пользователя.
     * @param isAdmin является ли пользователь администратором.
     */
    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
        if (isAdmin) {
            this.roles.add(Role.ADMIN);
        }
        this.roles.add(Role.USER);
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Возвращает имя пользователя в виде строки.
     * @return имя пользователя.
     */
    @Override
    public String toString() {
        return username;
    }

}
