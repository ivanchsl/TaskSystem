package com.example.tasksystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Конфигурация безопасности для Spring Security.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Компонент аутентификации, используемый для аутентификации пользователей.
     */
    @Autowired
    AuthProvider authProvider;

    /**
     * Конфигурирует менеджер аутентификации, чтобы использовать собственный компонент аутентификации.
     * @param auth AuthenticationManagerBuilder, используемый для настройки менеджера аутентификации.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

    /**
     * Конфигурирует разрешённые веб-страницы.
     * @param http HttpSecurity, используемый для настройки разрешённых веб-страниц.
     * @throws Exception если возникает ошибка при настройке.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/reg", "/reg/form").anonymous()
                .antMatchers("/tasks", "/tasks/new", "/tasks/new/form", "/tasks/form").authenticated()
                .anyRequest().denyAll()
                .and().formLogin()
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").permitAll();
    }

}
