package com.example.tasksystem.controllers;

import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IndexControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    @InjectMocks
    private IndexController indexController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
    }

    @Test
    void index_WhenPrincipalIsNull_ReturnsIndex() {
        String result = indexController.index(null, model);
        assertThat(result).isEqualTo("index");
    }

    @Test
    void index_WhenPrincipalIsNotNull_ReturnsIndexLogged() {
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        String result = indexController.index(principal, model);

        assertThat(result).isEqualTo("index-logged");
        verify(principal).getName();
        verify(userRepository).findByUsername("testUser");
        verify(model).addAttribute("user", user);
    }
}
