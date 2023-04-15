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

@Controller
public class TasksController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/tasks")
    public String tasks(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        List<Task> tasks = user.getTasks();
        tasks.sort(new TaskComparator());
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String newTask(Principal principal) {
        if (principal == null) {
            return "redirect:/";
        }
        return "tasks-new";
    }

    @GetMapping("/tasks/new/form")
    public String newTaskForm(Principal principal, @RequestParam String title, @RequestParam String description, @RequestParam String deadline) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        taskRepository.save(new Task(title, description, user, LocalDate.parse(deadline)));
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/form")
    public String removePostTaskPage(Principal principal, @RequestParam String taskId, @RequestParam(required = false) String action) {
        if ((action == null) || (principal == null)) {
            return "redirect:/tasks";
        }
        User user = userRepository.findByUsername(principal.getName());
        Optional<Task> task = taskRepository.findById(parseLong(taskId));
        if (task.isEmpty()) {
            return "redirect:/tasks";
        }
        taskRepository.delete(task.get());
        return "redirect:/tasks";
    }

}

class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return o1.getDeadline().compareTo(o2.getDeadline());
    }

}
