package com.example.tasksystem.config;

import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Компонент аутентификации для Spring Security.
 */
@Component
public class AuthProvider implements AuthenticationProvider {

    /**
     * Репозиторий пользователей, используемый для поиска пользователей в базе данных.
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Создает новый bean PasswordEncoder для шифрования паролей пользователей.
     * @return PasswordEncoder для шифрования паролей пользователей.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Аутентифицирует пользователя на основе его имени и пароля.
     * @param authentication объект Authentication, содержащий имя пользователя и пароль.
     * @return объект Authentication, представляющий аутентифицированного пользователя.
     * @throws BadCredentialsException если имя пользователя или пароль неверны.
     */
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = userRepository.findByUsername(username);

        if (user != null && (user.getUsername().equals(username))) {
            if(!passwordEncoder().matches(password, user.getPassword())) {
                throw new BadCredentialsException("Wrong password");
            }
            Collection<? extends GrantedAuthority> authorities = user.getRoles();
            return new UsernamePasswordAuthenticationToken(user, password, authorities);
        } else {
            throw new BadCredentialsException("Username not found");
        }
    }

    /**
     * Проверяет, поддерживается ли данный тип аутентификации.
     * @param arg тип аутентификации, поддержка которого должна быть проверена.
     * @return true, поскольку единственный возможный тип аутентификации поддерживается.
     */
    public boolean supports(Class<?> arg) {
        return true;
    }

}
