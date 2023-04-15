package com.example.tasksystem.controllers;

import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для страницы регистрации.
 */
@Controller
public class RegistrationController {

    /**
     * Репозиторий пользователей, используемый для сохранения пользователей в базе данных.
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Кодировщик паролей, используемый для шифрования паролей пользователей.
     */
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Отображает страницу регистрации.
     * @return имя файла веб-страницы.
     */
    @GetMapping("/reg")
    public String reg() {
        return "reg";
    }

    /**
     * Обрабатывает форму регистрации.
     * Сохраняет нового пользователя в базе данных.
     * @param username имя пользователя, введённое в форме регистрации.
     * @param password пароль пользователя, введённый в форме регистрации.
     * @return шаблон перенаправления.
     */
    @GetMapping("/reg/form")
    public String regForm(@RequestParam String username, @RequestParam String password) {
        userRepository.save(new User(username, passwordEncoder.encode(password)));
        return "redirect:/";
    }

}
