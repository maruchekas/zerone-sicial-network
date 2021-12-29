package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(value = {"classpath:application.yml"})
@TestPropertySource(value = {"classpath:application-test.properties"})
public class ProfileControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;

    private Person verifyPerson;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String verifyEmail = "test1@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now();
        String conf_code = "123";

        verifyPerson = new Person()
                .setEmail(verifyEmail)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now());
        personRepository.save(verifyPerson);
    }

    @AfterEach
    public void cleanup() {
        personRepository.delete(verifyPerson);
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void deletePerson() throws Exception {
        Assertions.assertEquals(verifyPerson.getEmail(), "test1@test.ru");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/me")
                        .principal(() -> "test1@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertNotEquals("Пользователь не удален", verifyPerson.getEmail(), "test@test.ru");
        Assertions.assertEquals(LocalDateTime.now().getDayOfMonth(),
                personRepository.findByEmail(verifyPerson.getEmail()).get().getLastOnlineTime().getDayOfMonth());
    }


    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void getPerson() throws Exception {
        Person person = new Person()
                .setFirstName("Dmitriy")
                .setLastName("Sushkov")
                .setEmail("kkki@test.ru");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals(verifyPerson.getEmail(), email);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/users/me")
                .principal(() -> "test1@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("string"));

        Assertions.assertEquals(LocalDateTime.now().getDayOfMonth(),
                personRepository.findByEmail(verifyPerson.getEmail()).get().getLastOnlineTime().getDayOfMonth());
    }
}
