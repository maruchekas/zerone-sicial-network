package com.skillbox.javapro21.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
class PlatformControllerTest {

    @Autowired
    MockMvc mockMvc;



    @Test
    @WithMockUser(username = "person@test.ru", authorities = "user:write")
    void getCountries() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/platform/countries")
                        .principal(() -> "person@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", containsInAnyOrder("Россия", "Австралия")));
    }

    @Test
    @WithMockUser(username = "person@test.ru", authorities = "user:write")
    void getCitiesSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/platform/cities?country=Россия")
                        .principal(() -> "person@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",
                        containsInAnyOrder("Москва", "Абрамцево", "Алабино", "Апрелевка", "Архангельское")));
    }

    @Test
    @WithMockUser(username = "person@test.ru", authorities = "user:write")
    void getCitiesEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/platform/cities?country=Япония")
                        .principal(() -> "person@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(0)));
    }
}