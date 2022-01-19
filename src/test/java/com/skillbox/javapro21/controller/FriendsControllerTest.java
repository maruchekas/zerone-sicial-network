package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.domain.Dialog;
import com.skillbox.javapro21.domain.Message;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.DialogRepository;
import com.skillbox.javapro21.repository.MessageRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PersonToDialogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
public class FriendsControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PersonToDialogRepository personToDialogRepository;
    @Autowired
    private MessageRepository messageRepository;

    private Person verifyPerson;
    private Person verifyPersonWithPost;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String verifyEmail = "test@test.ru";
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
                .setLastOnlineTime(LocalDateTime.now())
                .setBirthDate(LocalDateTime.of(2000, 1, 1, 10, 0))
                .setCountry("Россия")
                .setTown("Москва");
        personRepository.save(verifyPerson);

        verifyPersonWithPost = new Person()
                .setEmail(verifyEmail + "b")
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName + "-Канефоль")
                .setLastName(lastName)
                .setConfirmationCode("123")
                .setRegDate(reg_date)
                .setConfirmationCode(conf_code)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now().minusDays(2))
                .setBirthDate(LocalDateTime.of(2010, 1, 1, 10, 0))
                .setCountry("Россия")
                .setTown("Санкт-Петербург");
        personRepository.save(verifyPersonWithPost);


    }


}
