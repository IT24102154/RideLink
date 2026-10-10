package com.readlink.account_service;

import com.ridelink.account_service.dto.AuthResponse;
import com.ridelink.account_service.repository.UserRepository;
import com.ridelink.account_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.ridelink.account_service.AccountServiceApplication.class)
@AutoConfigureMockMvc
class AccountServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    // Mock out MongoDB dependencies so context loads without a live DB
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Test
    void testRegisterV1EndpointStatus() throws Exception {
        when(authService.registerUser(any())).thenReturn("User registered successfully!");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content("{\"name\":\"Test\",\"email\":\"test@test.com\",\"password\":\"123456\",\"phoneNumber\":\"1234567890\"}"))
                .andExpect(status().isOk());
    }

    // Test removed

    @Test
    void testLoginEndpointStatus() throws Exception {
        when(authService.loginUser(any())).thenReturn(
                new AuthResponse("token123", "id1", "test@test.com", Set.of("ROLE_USER")));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content("{\"email\":\"test@test.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());
    }
}
