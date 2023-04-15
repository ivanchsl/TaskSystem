package com.example.tasksystem.models;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Класс, представляющий задачу в системе.
 * Имеет поля для заголовка, описания, даты начала, даты дедлайна, стиля отображения, а также связь с пользователем, которому задача принадлежит.
 */
@Getter
@Setter
@Entity
public class Task {

    /**
     * Идентификатор задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Заголовок задачи.
     */
    private String title;

    /**
     * Описание задачи.
     */
    private String description;

    /**
     * Дата начала задачи.
     */
    private LocalDate start;

    /**
     * Дата дедлайна.
     */
    private LocalDate deadline;

    /**
     * Стиль задачи, используемый для отображения на странице задач.
     */
    private String style;

    /**
     * Пользователь, которому принадлежит задача.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Создает новую задачу без параметров.
     */
    public Task() {
    }

    /**
     * Создает новую задачу с заданными параметрами.
     * @param title заголовок задачи.
     * @param description описание задачи.
     * @param user пользователь, которому принадлежит задача.
     * @param deadline дата дедлайна.
     */
    public Task(String title, String description, User user, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.start = LocalDate.now();
        this.deadline = deadline;
        updateStyle();
    }

    /**
     * Обновляет стиль задачи на основе даты дедлайна.
     */
    public void updateStyle() {
        style = "color:black";
        if (deadline.isBefore(LocalDate.now().plusDays(11))) {
            style = "color:green";
        }
        if (deadline.isBefore(LocalDate.now().plusDays(4))) {
            style = "color:yellow";
        }
        if (deadline.isBefore(LocalDate.now().plusDays(2))) {
            style = "color:orange";
        }
        if (deadline.isBefore(LocalDate.now().plusDays(1))) {
            style = "color:red";
        }
    }

}
