package com.example.tasksystem.models;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDate start;

    private LocalDate deadline;

    private String style;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Task() {
    }

    public Task(String title, String description, User user, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.start = LocalDate.now();
        this.deadline = deadline;
        updateStyle();
    }

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
