package com.example.tasksystem.controllers;

import com.example.tasksystem.models.Task;
import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.TaskRepository;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.lang.Long.parseLong;

/**
 * Контроллер для обработки запросов, связанных с задачами.
 * Обрабатывает запросы на отображение списка задач пользователя, создание новой задачи и удаление задачи.
 */
@Controller
public class TasksController {

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
     * Обрабатывает запрос на отображение списка задач пользователя.
     * Если пользователь не авторизован, перенаправляет на главную страницу.
     * Сортирует задачи пользователя по дедлайну и добавляет их в модель для отображения на странице.
     * @param principal объект, содержащий информацию об авторизованном пользователе.
     * @param model модель для передачи данных на веб-страницу.
     * @param message сообщение для пользователя.
     * @return имя файла веб-страницы или шаблон перенаправления.
     */
    @GetMapping("/tasks")
    public String tasks(Principal principal, Model model, @RequestParam(required = false) String message) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        List<Task> tasks = user.getTasks();
        tasks.sort(new TaskComparator());
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", user);
        model.addAttribute("modified", user.getLastModified().toString());
        model.addAttribute("message", message);
        return "tasks";
    }

    /**
     * Обрабатывает запрос на получение времени последнего изменения списка задач пользователя.
     * @param principal объект, содержащий информацию об авторизованном пользователе.
     * @return искомое время.
     */
    @GetMapping("/getLastModified")
    @ResponseBody
    public String getLastModified(Principal principal) {
        if (principal == null) {
            return "";
        }
        User user = userRepository.findByUsername(principal.getName());
        return user.getLastModified().toString();
    }

    /**
     * Обрабатывает запрос на отображение формы для создания новой задачи.
     * Если пользователь не авторизован, перенаправляет на главную страницу.
     * @param principal объект, содержащий информацию об авторизованном пользователе.
     * @return имя файла веб-страницы или шаблон перенаправления.
     */
    @GetMapping("/tasks/new")
    public String newTask(Principal principal) {
        if (principal == null) {
            return "redirect:/";
        }
        return "tasks-new";
    }

    /**
     * Обрабатывает запрос на создание новой задачи.
     * Если пользователь не авторизован, перенаправляет на главную страницу.
     * Создаёт новую задачу с указанными параметрами и сохраняет её в базе данных.
     * @param principal объект, содержащий информацию об авторизованном пользователе.
     * @param title заголовок новой задачи.
     * @param description описание новой задачи.
     * @param deadline крайний срок выполнения новой задачи.
     * @return перенаправление на страницу со списком задач пользователя.
     */
    @GetMapping("/tasks/new/form")
    public String newTaskForm(Principal principal, @RequestParam String title, @RequestParam String description, @RequestParam String deadline) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        taskRepository.save(new Task(title, description, user, LocalDate.parse(deadline), 0));
        userRepository.save(user);
        return "redirect:/tasks";
    }

    /**
     * Обрабатывает запрос на изменение задачи.
     * @param taskId идентификатор задачи, над которой производится действие.
     * @param userId идентификатор пользователя, которому нужно передать задачу.
     * @param action действие, которое нужно выполнить.
     * @return перенаправление на страницу со списком задач пользователя с, возможно, сообщением.
     */
    @GetMapping("/tasks/form")
    public String taskForm(Principal principal, @RequestParam String taskId, @RequestParam(required = false) String userId, @RequestParam(required = false) String action) {
        if (principal == null) {
            return "redirect:/tasks?message=Error: You must be authorized.";
        }
        User user = userRepository.findByUsername(principal.getName());
        Optional<Task> optionalTask = taskRepository.findById(parseLong(taskId));
        if (optionalTask.isEmpty()) {
            return "redirect:/tasks?message=Error: Task not found.";
        }
        Task task = optionalTask.get();
        if (!task.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks?message=Error: You can't do anything with other user's task.";
        }
        if ("throw".equals(action)) {
            if (task.getThrowable() != 0) {
                return "redirect:/tasks?message=Error: Task is not throwable.";
            }
            Optional<User> optionalUser = userRepository.findById(parseLong(userId));
            if (optionalUser.isEmpty()) {
                return "redirect:/tasks?message=Error: Target user not found.";
            }
            User targetUser = optionalUser.get();
            task.setUser(targetUser);
            taskRepository.save(task);
            targetUser.setLastModified(LocalDateTime.now());
            userRepository.save(targetUser);
        }
        if ("remove".equals(action)) {
            if (task.getThrowable() != 0) {
                return "redirect:/tasks?message=Error: You must complete this task.";
            }
            taskRepository.delete(task);
        }
        if ("complete".equals(action)) {
            if (task.getThrowable() == 0) {
                return "redirect:/tasks?message=Error: Use Remove action.";
            }
            task.setThrowable(-1);
            taskRepository.save(task);
        }
        return "redirect:/tasks";
    }

}

/**
 * Компаратор для сортировки списка задач.
 */
class TaskComparator implements Comparator<Task> {

    /**
     * Сравнивает две задачи по дедлайну.
     * @param o1 первая задача для сравнения.
     * @param o2 вторая задача для сравнения.
     * @return отрицательное число, ноль или положительное число в зависимости от того, какая задача должна идти первой в списке.
     */
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.isCompleted() && !o2.isCompleted()) {
            return 1;
        } else if (!o1.isCompleted() && o2.isCompleted()) {
            return -1;
        } else {
            return o1.getDeadline().compareTo(o2.getDeadline());
        }
    }

}
