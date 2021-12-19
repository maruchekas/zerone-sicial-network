package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;
import com.skillbox.javapro21.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.yml"})
public class AccountControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private JwtGenerator jwtGenerator;

    private Person person;
    private Person verifyPerson;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String email = "arcadiy@test.ru";
        String verifyEmail = "test@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";

        person = new Person();
        person.setEmail(email);
        person.setPassword(passwordEncoder.encode(password));
        person.setFirstName(firstName);
        person.setLastName(lastName);

        verifyPerson = new Person();
        verifyPerson.setEmail(verifyEmail);
        verifyPerson.setPassword(passwordEncoder.encode(password));
        verifyPerson.setFirstName(firstName);
        verifyPerson.setLastName(lastName);
        verifyPerson.setConfirmationCode("123");

        personRepository.save(verifyPerson);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
    }

    @Test
    void registrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(person.getFirstName());
        registerRequest.setLastName(person.getLastName());
        registerRequest.setEmail(person.getEmail());
        registerRequest.setPasswd1(person.getPassword());
        registerRequest.setPasswd2(person.getPassword());
        registerRequest.setCode(person.getPassword());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/vi/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void verifyRegistration() throws Exception {
        verifyPerson.setConfirmationCode("123");
        String json = "{\"email\": \"Arcadiy\", \"code\": \"123\"}";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/vi/account/register/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void recoveryPasswordMessage() throws Exception {
        RecoveryRequest recoveryRequest = new RecoveryRequest();
        recoveryRequest.setEmail(verifyPerson.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/vi/account/password/send_recovery_massage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(recoveryRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void verifyRecovery() throws Exception {
        String json = "{\"email\":\"test@test.ru\", \"code\":\"123\"}";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/vi/account/password/recovery/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void recoveryPassword() throws Exception {
        String json = "{\"email\": \"test@test.ru\", \"password\": \"1234\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/vi/account/password/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test@test.ru")
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void changePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setPassword(verifyPerson.getPassword());
        changePasswordRequest.setToken(jwtGenerator.generateToken(verifyPerson.getEmail()));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/vi/account/password/set")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void changeEmail() throws Exception {
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest();
        changeEmailRequest.setEmail(verifyPerson.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/vi/account/email")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changeEmailRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void changeNotifications() throws Exception{
        ChangeNotificationsRequest changeNotificationsRequest = new ChangeNotificationsRequest();
        changeNotificationsRequest.setNotificationTypeStatus(NotificationTypeStatus.MESSAGE);
        changeNotificationsRequest.setEnable(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/vi/account/notifications")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changeNotificationsRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
