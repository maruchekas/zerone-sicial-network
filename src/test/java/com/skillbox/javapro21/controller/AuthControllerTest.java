package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.auth.AuthRequest;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import org.junit.jupiter.api.Assertions;
import com.skillbox.javapro21.service.serviceImpl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.yml"})
public class AuthControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private JwtGenerator jwtGenerator;

    private Person person;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String email = "test@test.ru";
        String password = "111111111";

        person = authService.findPersonByEmail(email);

    }

    @Test
    public void loginTest() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(person.getEmail());
        authRequest.setPassword("111111111");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void incorrectPasswordLoginTest() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(person.getEmail());
        authRequest.setPassword("000000000");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
    }

    @Test
    public void incorrectEmailLoginTest() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test0@test.ru");
        authRequest.setPassword("111111111");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
    }


    @Test
    void testToken() {
        String bec = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJicml6Lnp1a2tlbEBnbWFpbC5jb20iLCJleHAiOjE2NDEzMzAwMDB9.S4k0Q26X3iV7AJdqMbJgtAws3NpgM-4_kyAf3m9kyPJMY2OHLQcTZHGoEgdhnRKDFCQW215bcGcd8upXvZ_ulg";
        String front = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJicml6Lnp1a2tlbEBnbWFpbC5jb20iLCJleHAiOjE2NDEzMzAwMDB9.S4k0Q26X3iV7AJdqMbJgtAws3NpgM-4_kyAf3m9kyPJMY2OHLQcTZHGoEgdhnRKDFCQW215bcGcd8upXvZ_ulg";
        Assertions.assertEquals(bec, front);
    }
}
