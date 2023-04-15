package com.example.tasksystem.controllers;

import com.example.tasksystem.models.Task;
import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.TaskRepository;
import com.example.tasksystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksControllerTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TasksController tasksController;

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    private User user;
    private List<Task> tasks;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        tasks = Arrays.asList(
                new Task("Task1", "Description1", user, LocalDate.now()),
                new Task("Task2", "Description2", user, LocalDate.now().plusDays(1))
        );
        user.setTasks(tasks);
    }

    @Test
    void testTasksIfAuthorized() {
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        String result = tasksController.tasks(principal, model);
        assertEquals("tasks", result);
        verify(userRepository).findByUsername("testuser");
        verify(model).addAttribute("tasks", tasks);
    }

    @Test
    void testTasksIfUnauthorized() {
        String result = tasksController.tasks(null, model);
        assertEquals("redirect:/", result);
    }

    @Test
    void testNewTaskIfAuthorized() {
        String result = tasksController.newTask(principal);
        assertEquals("tasks-new", result);
    }

    @Test
    void testNewTaskIfUnauthorized() {
        String result = tasksController.newTask(null);
        assertEquals("redirect:/", result);
    }

    @Test
    void testNewTaskFormIfAuthorized() {
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        String title = "Test Task";
        String description = "Test Description";
        String deadline = LocalDate.now().toString();

        String result = tasksController.newTaskForm(principal, title, description, deadline);
        assertEquals("redirect:/tasks", result);
        verify(userRepository).findByUsername("testuser");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testNewTaskFormIfUnauthorized() {
        String title = "Test Task";
        String description = "Test Description";
        String deadline = LocalDate.now().toString();
        String result = tasksController.newTaskForm(null, title, description, deadline);
        assertEquals("redirect:/", result);
    }

    @Test
    void testTaskFormIfAuthorized() {
        String taskId = "1";
        String action = "delete";
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(taskRepository.findById(Long.parseLong(taskId))).thenReturn(Optional.of(tasks.get(0)));

        String result = tasksController.taskForm(principal, taskId, action);
        assertEquals("redirect:/tasks", result);
        verify(taskRepository).findById(Long.parseLong(taskId));
        verify(taskRepository).delete(tasks.get(0));
    }

    @Test
    void testTaskFormIfUnauthorized() {
        String taskId = "1";
        String action = "delete";
        String result = tasksController.taskForm(null, taskId, action);
        assertEquals("redirect:/tasks", result);
    }

    @Test
    void testTaskFormIfWrongTask() {
        String taskId = "1";
        String action = "delete";
        when(taskRepository.findById(Long.parseLong(taskId))).thenReturn(Optional.empty());
        String result = tasksController.taskForm(principal, taskId, action);
        assertEquals("redirect:/tasks", result);
    }

}