package com.uap.proiv.jobs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GraphqlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /graphql - query jobById devuelve el trabajo")
    void jobById() throws Exception {
        String body = "{\"query\":\"{ jobById(id: 1) { id name salary hours resources } }\"}";

        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jobById.id").value("1"))
                .andExpect(jsonPath("$.data.jobById.name").value("Data Engineer"))
                .andExpect(jsonPath("$.data.jobById.resources").value(3));
    }
}
