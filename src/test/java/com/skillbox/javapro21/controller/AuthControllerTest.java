package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.auth.AuthRequest;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import org.junit.jupiter.api.Assertions;
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
    private JwtGenerator jwtGenerator;

    private Person person;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String email = "test@test.ru";
        String password = "$2a$12$0nJaevWNb/X4gdZg.xBR1OSsHSq.CpU49.F68OMz1yn3CTO5xbqZi";

        person = new Person()
                .setEmail(email)
                .setPassword(passwordEncoder.encode(password));

    }

    @Test
    public void loginTest() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(person.getEmail());
        authRequest.setPassword(person.getPassword());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void correctPasswordTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test@test.ru")
                        .param("password", "123456"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void incorrectPasswordTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test@test.ru")
                        .param("password", "1234567"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testToken() {
        String bec = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJicml6Lnp1a2tlbEBnbWFpbC5jb20iLCJleHAiOjE2NDEzMzAwMDB9.S4k0Q26X3iV7AJdqMbJgtAws3NpgM-4_kyAf3m9kyPJMY2OHLQcTZHGoEgdhnRKDFCQW215bcGcd8upXvZ_ulg";
        String front = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJicml6Lnp1a2tlbEBnbWFpbC5jb20iLCJleHAiOjE2NDEzMzAwMDB9.S4k0Q26X3iV7AJdqMbJgtAws3NpgM-4_kyAf3m9kyPJMY2OHLQcTZHGoEgdhnRKDFCQW215bcGcd8upXvZ_ulg";
        Assertions.assertEquals(bec, front);
    }
}
