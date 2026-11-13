package com.uap.proiv.jobs.controller;

import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    private MockMvc mockMvc;
    private UserApiResponse userApiResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        User user1 = new User();
        user1.setId(10);
        user1.setFirstName("Juan");
        user1.setLastName("Garcia");
        User user2 = new User();
        user2.setId(20);
        user2.setFirstName("Diana");
        user2.setLastName("Diaz");

        userApiResponse = new UserApiResponse();
        userApiResponse.setPage(1);
        userApiResponse.setPerPage(2);
        userApiResponse.setTotal(2);
        userApiResponse.setTotalPages(1);
        userApiResponse.setData(List.of(user1, user2));
    }

    @Test
    @DisplayName("GET /api/user/{page} retorna usuarios")
    void getUsers_success() throws Exception {
        when(userService.search(1)).thenReturn(userApiResponse);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    @DisplayName("GET /api/user/{page} - excepcion retornada por el service")
    void getUsers_exception() throws Exception {
        when(userService.search(2)).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/user/2"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Service error"));
    }
}
