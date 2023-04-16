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

import java.security.Principal;
import java.time.LocalDate;
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
     * @return имя файла веб-страницы или шаблон перенаправления.
     */
    @GetMapping("/tasks")
    public String tasks(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        List<Task> tasks = user.getTasks();
        tasks.sort(new TaskComparator());
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", userRepository.findAll());
        return "tasks";
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
        taskRepository.save(new Task(title, description, user, LocalDate.parse(deadline)));
        return "redirect:/tasks";
    }

    /**
     * Обрабатывает запрос на удаление задачи.
     * @param taskId идентификатор задачи, которую нужно удалить.
     * @param userId идентификатор пользователя, которому нужно передать задачу.
     * @param action действие, которое нужно выполнить: передача или удаление задачи.
     * @return перенаправление на страницу со списком задач пользователя.
     */
    @GetMapping("/tasks/form")
    public String taskForm(Principal principal, @RequestParam String taskId, @RequestParam String userId, @RequestParam(required = false) String action) {
        String redirect = "redirect:/tasks";
        if (principal == null) {
            return redirect;
        }
        User user = userRepository.findByUsername(principal.getName());
        Optional<Task> optionalTask = taskRepository.findById(parseLong(taskId));
        if (optionalTask.isEmpty()) {
            return redirect;
        }
        Task task = optionalTask.get();
        if (!task.getUser().getId().equals(user.getId())) {
            return redirect;
        }
        if ("throw".equals(action)) {
            Optional<User> optionalUser = userRepository.findById(parseLong(userId));
            if (optionalUser.isEmpty()) {
                return redirect;
            }
            task.setUser(optionalUser.get());
            taskRepository.save(task);
        }
        if ("remove".equals(action)) {
            taskRepository.delete(task);
        }
        return redirect;
    }

}

/**
 * Компаратор для сортировки списка задач по дедлайну.
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
        return o1.getDeadline().compareTo(o2.getDeadline());
    }

}
