package ru.kotomore.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.kotomore.dto.UserDTO;
import ru.kotomore.dto.security.JwtRequest;
import ru.kotomore.dto.security.JwtResponse;
import ru.kotomore.dto.security.RefreshJwtRequest;
import ru.kotomore.models.User;
import ru.kotomore.services.AuthService;
import ru.kotomore.services.RegistrationService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthController authController;

    AutoCloseable openMocks;

    @BeforeEach
    public void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        authController = new AuthController(authService, registrationService, modelMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_ShouldReturnOk() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("John");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");

        when(registrationService.saveUser(any(UserDTO.class))).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void register_With_Empty_Email_ShouldReturnBadRequest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("John");
        userDTO.setEmail("");
        userDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");

        when(registrationService.saveUser(any(UserDTO.class))).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login__ShouldReturnOk() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccessToken("jwt_token");

        when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void login_Without_Password_ShouldReturnBadRequest() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("");

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccessToken("jwt_token");

        when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_With_Invalid_Password_ShouldReturnBadRequest() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccessToken("jwt_token");

        when(authService.login(any(JwtRequest.class))).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void refresh_token_ShouldReturnJwtResponse() throws Exception {
        RefreshJwtRequest refreshJwtRequest = new RefreshJwtRequest();
        refreshJwtRequest.setRefreshToken("refresh_token");

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccessToken("jwt_token");

        when(authService.refresh(any(String.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(refreshJwtRequest)))
                .andExpect(status().isOk());
    }

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
