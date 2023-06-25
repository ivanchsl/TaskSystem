package com.example.tasksystem.controllers;

import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @InjectMocks
    private RegistrationController registrationController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
    }

    @Test
    void reg_ReturnsRegView() {
        String result = registrationController.reg(model, null);
        verify(model).addAttribute("message", null);
        assertThat(result).isEqualTo("reg");
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void regForm_SavesUserAndDoesRedirect() {
        String username = "testUser";
        String password = "testPassword";
        String encodedPassword = "encodedTestPassword";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = registrationController.regForm(username, password);

        assertThat(result).isEqualTo("redirect:/");

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(argThat(user -> user.getUsername().equals(username) && user.getPassword().equals(encodedPassword)));
    }
}
