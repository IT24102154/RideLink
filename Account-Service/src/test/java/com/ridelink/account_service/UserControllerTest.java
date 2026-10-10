package com.ridelink.account_service;

import com.ridelink.account_service.controller.UserController;
import com.ridelink.account_service.dto.UpdateProfileRequest;
import com.ridelink.account_service.model.Role;
import com.ridelink.account_service.model.User;
import com.ridelink.account_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.ridelink.account_service.AccountServiceApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id("123")
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .roles(Set.of(Role.ROLE_PASSENGER))
                .active(true)
                .build();
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = {"PASSENGER"})
    void getProfile_Success() throws Exception {
        when(userService.getCurrentUserProfile("john@example.com")).thenReturn(mockUser);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = {"PASSENGER"})
    void updateProfile_Success() throws Exception {
        when(userService.updateProfile(eq("john@example.com"), any(UpdateProfileRequest.class)))
                .thenReturn(mockUser);

        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Jane Doe\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateStatus_Success() throws Exception {
        when(userService.updateAccountStatus(eq("123"), eq(false))).thenReturn(mockUser);

        mockMvc.perform(patch("/api/v1/users/123/status")
                .param("active", "false"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = {"PASSENGER"})
    void updateStatus_Forbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/users/123/status")
                .param("active", "false"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateRoles_Success() throws Exception {
        when(userService.updateRoles(eq("123"), any())).thenReturn(mockUser);

        mockMvc.perform(patch("/api/v1/users/123/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"ROLE_DRIVER\"]"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = {"PASSENGER"})
    void updateRoles_Forbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/users/123/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"ROLE_DRIVER\"]"))
                .andExpect(status().isForbidden());
    }
}
