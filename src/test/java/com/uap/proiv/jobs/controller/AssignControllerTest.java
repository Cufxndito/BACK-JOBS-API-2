package com.uap.proiv.jobs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.proiv.jobs.dto.AssignRequest;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.UserJobAssignedService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AssignControllerTest {
    @Mock
    UserJobAssignedService userJobAssignedService;

    @InjectMocks
    AssignController assignController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AssignRequest assignRequest;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(assignController).build();
        assignRequest = new AssignRequest();
        assignRequest.setRequestNumber(123);
        assignRequest.setClientName("Name");
    }

    @Test
    @DisplayName("POST /api/assign retorna las asignaciones")
    void postAssign_success() throws Exception {
        User user = new User();
        user.setId(10);
        user.setFirstName("Juan");
        Job job1 = new Job("Developer", 500, 80, 1, 3);
        Job job2 = new Job("Tester", 400, 75, 2, 2);

        when(userJobAssignedService.assign()).thenReturn(List.of(
                new UserJobAssigned(List.of(user), job1),
                new UserJobAssigned(List.of(user), job2)));

        mockMvc.perform(post("/api/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Assign").isNotEmpty())
                .andExpect(jsonPath("$.Assign[0].job.name").value("Developer"))
                .andExpect(jsonPath("$.Assign[1].job.name").value("Tester"))
                .andExpect(jsonPath("$.Assign[1].users[0].first_name").value("Juan"))
                .andExpect(jsonPath("$.Request_Number").value(123))
                .andExpect(jsonPath("$.Client").value("Name"));
    }

    @Test
    @DisplayName("POST /api/assign - excepcion del service devuelve error de servidor")
    void postAssign_exception() throws Exception {
        when(userJobAssignedService.assign()).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/api/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("boom"));
    }
}
