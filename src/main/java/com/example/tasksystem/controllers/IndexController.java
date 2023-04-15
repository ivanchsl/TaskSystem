package com.example.tasksystem.controllers;

import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Контроллер для главной страницы.
 */
@Controller
public class IndexController {

    /**
     * Репозиторий пользователей, используемый для поиска пользователей в базе данных.
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Отображает главную страницу.
     * Если пользователь не аутентифицирован, отображается стандартная главная страница.
     * Если пользователь аутентифицирован, отображается главная страница с приветствием пользователя.
     * @param principal объект Principal, содержащий имя текущего пользователя.
     * @param model объект Model, используемый для передачи данных в шаблон представления.
     * @return имя файла веб-страницы.
     */
    @GetMapping("/")
    public String index(Principal principal, Model model) {
        if (principal == null) {
            return "index";
        }
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("username", user.getUsername());
        return "index-logged";
    }

}
