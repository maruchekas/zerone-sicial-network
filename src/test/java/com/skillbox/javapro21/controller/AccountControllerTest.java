package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
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

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
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
        String email = "ivan_zukkel@mail.ru";
        String verifyEmail = "test@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now();
        String conf_code = "123";

        person = new Person()
                .setEmail(email)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName);

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
                        .post("/api/v1/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void verifyRegistration() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/register/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test@test.ru")
                        .param("code", "123"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void recoveryPasswordMessage() throws Exception {
        RecoveryRequest recoveryRequest = new RecoveryRequest();
        recoveryRequest.setEmail(verifyPerson.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/account/password/send_recovery_massage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(recoveryRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void verifyRecovery() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/password/recovery/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test@test.ru")
                        .param("code", "123"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void recoveryPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/account/password/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test@test.ru")
                        .param("password", "1234"))
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
                        .put("/api/v1/account/password/set")
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
                        .put("/api/v1/account/email")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changeEmailRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void changeNotifications() throws Exception {
        ChangeNotificationsRequest changeNotificationsRequest = new ChangeNotificationsRequest();
        changeNotificationsRequest.setNotificationTypeStatus(NotificationTypeStatus.MESSAGE);
        changeNotificationsRequest.setEnable(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/account/notifications")
                        .principal(() -> "test@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changeNotificationsRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.ru", authorities = "user:write")
    void getNotifications() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/notifications")
                        .principal(() -> "test@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
