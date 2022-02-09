package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.account.*;
import com.skillbox.javapro21.config.security.JwtGenerator;
import com.skillbox.javapro21.domain.CaptchaCode;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.CaptchaRepository;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus.MESSAGE;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
public class AccountControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CaptchaRepository captchaRepository;
    @Autowired
    private JwtGenerator jwtGenerator;

    private Person person;
    private Person verifyPerson;
    private CaptchaCode captcha;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String email = "test222@test.ru";
        String verifyEmail = "test1@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now();
        String conf_code = "4321";

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
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now());
        personRepository.save(verifyPerson);

        captcha = new CaptchaCode()
                .setCode("1234")
                .setSecretCode("4321")
                .setTime(Timestamp.valueOf(LocalDateTime.now()));
        captchaRepository.save(captcha);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
        captchaRepository.deleteAll();
    }

    @Test
    void registrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(person.getFirstName());
        registerRequest.setLastName(person.getLastName());
        registerRequest.setEmail(person.getEmail());
        registerRequest.setPasswd1(person.getPassword());
        registerRequest.setPasswd2(person.getPassword());
        registerRequest.setCaptcha(captcha.getCode());
        registerRequest.setCaptchaSecret(captcha.getSecretCode());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void badRegistrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(person.getFirstName());
        registerRequest.setLastName(person.getLastName());
        registerRequest.setEmail(person.getEmail());
        registerRequest.setPasswd1(person.getPassword());
        registerRequest.setPasswd2(person.getPassword());
        registerRequest.setCaptcha("1111");
        registerRequest.setCaptchaSecret(captcha.getSecretCode());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("captcha"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void verifyRegistration() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/register/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test1@test.ru")
                        .param("code", "4321"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    void recoveryPasswordMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/password/send_recovery_massage")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(recoveryRequest))
                        .param("email", "test1@test.ru")
                        .param("code", "4321")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void verifyRecovery() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/password/recovery/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "test1@test.ru")
                        .param("password", "123"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void recoveryPassword() throws Exception {
        RecoveryRequest recoveryRequest = new RecoveryRequest();
        recoveryRequest.setEmail(verifyPerson.getEmail());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/account/password/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(recoveryRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void changePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setPassword(verifyPerson.getPassword());
        changePasswordRequest.setToken(jwtGenerator.generateToken(verifyPerson.getEmail()));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/account/password/set")
                        .principal(() -> "test1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void changeEmail() throws Exception {
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest();
        changeEmailRequest.setEmail(verifyPerson.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/account/email")
                        .principal(() -> "test1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changeEmailRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void changeNotifications() throws Exception {
        ChangeNotificationsRequest changeNotificationsRequest = new ChangeNotificationsRequest();
        changeNotificationsRequest.setNotificationTypeStatus(MESSAGE);
        changeNotificationsRequest.setEnable(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/account/notifications")
                        .principal(() -> "test1@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changeNotificationsRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test1@test.ru", authorities = "user:write")
    void getNotifications() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/notifications")
                        .principal(() -> "test1@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
