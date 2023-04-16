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
import java.time.LocalDateTime;
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
        user.setId(1L);
        user.setLastModified(LocalDateTime.of(2023, 4, 1, 10, 0));
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

        String result = tasksController.tasks(principal, model, null);
        assertEquals("tasks", result);
        verify(userRepository).findByUsername("testuser");
        verify(model).addAttribute("tasks", tasks);
    }

    @Test
    void testTasksIfUnauthorized() {
        String result = tasksController.tasks(null, model, null);
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
    void testTaskFormRemoveIfAuthorized() {
        String taskId = "1";
        String userId = "1";
        String action = "remove";
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(taskRepository.findById(Long.parseLong(taskId))).thenReturn(Optional.of(tasks.get(0)));

        String result = tasksController.taskForm(principal, taskId, userId, action);
        assertEquals("redirect:/tasks", result);
        verify(taskRepository).findById(Long.parseLong(taskId));
        verify(taskRepository).delete(tasks.get(0));
    }

    @Test
    void testTaskFormThrowIfAuthorized() {
        String taskId = "1";
        String userId = "1";
        String action = "throw";
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findById(Long.parseLong(userId))).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(taskRepository.findById(Long.parseLong(taskId))).thenReturn(Optional.of(tasks.get(0)));

        String result = tasksController.taskForm(principal, taskId, userId, action);
        assertEquals("redirect:/tasks", result);
        verify(taskRepository).findById(Long.parseLong(taskId));
        verify(taskRepository).save(tasks.get(0));
    }

    @Test
    void testTaskFormThrowIfBlankUser() {
        String taskId = "1";
        String userId = "1";
        String action = "throw";
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findById(Long.parseLong(userId))).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(taskRepository.findById(Long.parseLong(taskId))).thenReturn(Optional.of(tasks.get(0)));

        String result = tasksController.taskForm(principal, taskId, userId, action);
        assertEquals("redirect:/tasks?message=Error: Target user not found.", result);
        verify(taskRepository).findById(Long.parseLong(taskId));
        verify(taskRepository, never()).save(tasks.get(0));
    }

    @Test
    void testTaskFormThrowIfWrongUser() {
        String taskId = "1";
        String userId = "1";
        String action = "throw";
        User user2 = new User();
        user2.setId(2L);
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user2);
        when(taskRepository.findById(Long.parseLong(taskId))).thenReturn(Optional.of(tasks.get(0)));

        String result = tasksController.taskForm(principal, taskId, userId, action);
        assertEquals("redirect:/tasks?message=Error: You can't do anything with other user's task.", result);
        verify(taskRepository).findById(Long.parseLong(taskId));
        verify(taskRepository, never()).save(tasks.get(0));
    }

    @Test
    void testTaskFormIfUnauthorized() {
        String taskId = "1";
        String userId = "1";
        String action = "delete";
        String result = tasksController.taskForm(null, taskId, userId, action);
        assertEquals("redirect:/tasks?message=Error: You must be authorized.", result);
    }

    @Test
    void testTaskFormIfWrongTask() {
        String taskId = "1";
        String userId = "1";
        String action = "delete";
        when(taskRepository.findById(Long.parseLong(taskId))).thenReturn(Optional.empty());
        String result = tasksController.taskForm(principal, taskId, userId, action);
        assertEquals("redirect:/tasks?message=Error: Task not found.", result);
    }

    @Test
    void testGetLastModifiedIfAuthorized() {
        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        String result = tasksController.getLastModified(principal);
        assertEquals("2023-04-01T10:00", result);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testGetLastModifiedIfUnauthorized() {
        String result = tasksController.getLastModified(null);
        assertEquals("", result);
    }

}
