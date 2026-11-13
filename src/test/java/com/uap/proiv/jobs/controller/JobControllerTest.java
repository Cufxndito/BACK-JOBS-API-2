package com.uap.proiv.jobs.controller;

import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.service.JobService;
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
import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {
    @Mock
    JobService jobService;

    @InjectMocks
    JobController jobController;

    private MockMvc mockMvc;
    private Job job1;
    private Job job2;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
        job1 = new Job("Developer", 500, 80, 1, 3);
        job2 = new Job("Tester", 400, 75, 2, 2);
    }

    @Test
    @DisplayName("GET /api/job/all retorna todos los trabajos")
    void getAllJobs_success() throws Exception {
        when(jobService.getAllJobs()).thenReturn(List.of(job1, job2));

        mockMvc.perform(get("/api/job/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Developer"))
                .andExpect(jsonPath("$[1].name").value("Tester"));
    }

    @Test
    @DisplayName("GET /api/job/all - excepcion retornada por el service")
    void getAllJobs_exception() throws Exception {
        when(jobService.getAllJobs()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/job/all"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Service error"));
    }

    @Test
    @DisplayName("GET /api/job/{id} retorna el trabajo")
    void getJobById_success() throws Exception {
        when(jobService.getJobById(1)).thenReturn(job1);

        mockMvc.perform(get("/api/job/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Developer"))
                .andExpect(jsonPath("$.salary").value(500.0));
    }

    @Test
    @DisplayName("GET /api/job/{id} - trabajo inexistente devuelve error de servidor")
    void getJobById_notFound() throws Exception {
        when(jobService.getJobById(99)).thenThrow(new NoSuchElementException("No value present"));

        mockMvc.perform(get("/api/job/99"))
                .andExpect(status().is5xxServerError());
    }
}
