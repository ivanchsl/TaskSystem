package com.example.tasksystem.controllers;

import com.example.tasksystem.components.Summariser;
import com.example.tasksystem.models.Task;
import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.TaskRepository;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.Long.parseLong;

/**
 * Контроллер для обработки запросов администратора.
 * Обрабатывает запросы на отображение списка задач пользователей, а также отображение сводки.
 */
@Controller
public class AdminController {

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
     * Компонент, создающий сводку для отображения.
     */
    @Autowired
    Summariser summariser;

    /**
     * Обрабатывает запрос на отображение списка задач пользователей.
     * @param model модель для передачи данных на веб-страницу.
     * @param userId идентификатор пользователя.
     * @param message сообщение для администратора.
     * @return имя файла веб-страницы.
     */
    @GetMapping("/admin")
    public String adminPanel(Model model, @RequestParam(required = false) String userId, @RequestParam(required = false) String message) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        if ((userId != null) && (!"".equals(userId))) {
            Optional<User> optionalUser = userRepository.findById(parseLong(userId));
            if (optionalUser.isPresent()) {
                User selectedUser = optionalUser.get();
                List<Task> tasks = selectedUser.getTasks();
                model.addAttribute("selectedUser", selectedUser);
                model.addAttribute("tasks", tasks);
            }
        } else {
            List<Task> tasks = taskRepository.findAll();
            model.addAttribute("tasks", tasks);
        }
        model.addAttribute("message", message);
        return "admin";
    }

    /**
     * Обрабатывает запрос на изменение задачи.
     * @param taskId идентификатор задачи, над которой производится действие.
     * @param action действие, которое нужно выполнить.
     * @return перенаправление на страницу со списком задач пользователей.
     */
    @GetMapping("/admin/form")
    public String adminForm(@RequestParam String taskId, @RequestParam String action) {
        Optional<Task> optionalTask = taskRepository.findById(parseLong(taskId));
        if (optionalTask.isEmpty()) {
            return "redirect:/admin?message=Error: Task not found.";
        }
        Task task = optionalTask.get();
        if ("remove".equals(action) || "accept".equals(action)) {
            taskRepository.delete(task);
        }
        if ("decline".equals(action)) {
            if (task.getThrowable() == 0) {
                return "redirect:/admin?message=Error: Use Remove action.";
            }
            task.setThrowable(1);
            taskRepository.save(task);
            User user = task.getUser();
            user.setLastModified(LocalDateTime.now());
            userRepository.save(user);
        }
        return "redirect:/admin";
    }

    /**
     * Обрабатывает запрос на создание новой задачи.
     * Создаёт новую задачу с указанными параметрами и сохраняет её в базе данных.
     * @param userId идентификатор пользователя.
     * @param title заголовок новой задачи.
     * @param description описание новой задачи.
     * @param deadline крайний срок выполнения новой задачи.
     * @return перенаправление на страницу со списком задач пользователя.
     */
    @GetMapping("/admin/newTaskForm")
    public String adminForm(@RequestParam String userId, @RequestParam String title, @RequestParam String description, @RequestParam String deadline) {
        Optional<User> optionalUser = userRepository.findById(parseLong(userId));
        if (optionalUser.isEmpty()) {
            return "redirect:/admin?message=Error: Target user not found.";
        }
        User targetUser = optionalUser.get();
        taskRepository.save(new Task(title, description, targetUser, LocalDate.parse(deadline), 1));
        targetUser.setLastModified(LocalDateTime.now());
        userRepository.save(targetUser);
        return "redirect:/admin";
    }

    /**
     * Обрабатывает запрос на отображение сводки.
     * @param model модель для передачи данных на веб-страницу.
     * @return имя файла веб-страницы.
     */
    @GetMapping("/summary")
    public String summary(Model model) {
        model.addAttribute("updated", summariser.getUpdated());
        model.addAttribute("numUsers", summariser.getNumUsers());
        model.addAttribute("numFreeUsers", summariser.getNumFreeUsers());
        model.addAttribute("numTasks", summariser.getNumTasks());
        model.addAttribute("numExpired", summariser.getNumExpired());
        model.addAttribute("numMissedDeadline", summariser.getNumMissedDeadline());
        model.addAttribute("numReview", summariser.getNumReview());
        return "summary";
    }

}
