package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.domain.Dialog;
import com.skillbox.javapro21.domain.Message;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.PersonToDialog;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.repository.DialogRepository;
import com.skillbox.javapro21.repository.MessageRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PersonToDialogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import static com.skillbox.javapro21.domain.enumeration.ReadStatus.SENT;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = {"classpath:application-test.properties"})
public class DialogsControllerTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private DialogRepository dialogRepository;
    @Autowired
    private PersonToDialogRepository personToDialogRepository;
    @Autowired
    private MessageRepository messageRepository;

    private Person person1;
    private Person person2;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String emailForOne = "test999@test.ru";
        String emailForTwo = "test1000@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now(ZoneOffset.UTC).minusDays(2);

        person1 = new Person()
                .setEmail(emailForOne)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRegDate(reg_date)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .setConfirmationCode("");
        Person sP1 = personRepository.save(person1);
        person2 = new Person()
                .setEmail(emailForTwo)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRegDate(reg_date)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .setConfirmationCode("");
        Person sP2 = personRepository.save(person2);

        Set<Person> personSet = new HashSet<>();
        personSet.add(sP1);
        personSet.add(sP2);

        Dialog dialog = new Dialog()
                .setPersons(personSet)
                .setCode("")
                .setIsBlocked(0)
                .setTitle("Test");
        Dialog sDialog = dialogRepository.save(dialog);

        Message message = new Message()
                .setTime(LocalDateTime.now(ZoneOffset.UTC))
                .setIsBlocked(0)
                .setAuthor(sP1)
                .setRecipient(sP2)
                .setDialog(sDialog)
                .setReadStatus(SENT)
                .setMessageText("wtf?");
        Message sMessage = messageRepository.save(message);
        Set<Message> messageSet = new HashSet<>();
        messageSet.add(sMessage);
        sDialog.setMessages(messageSet);
        dialogRepository.save(sDialog);

        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(sP1.getId(), sDialog.getId());
        p2d.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
        personToDialogRepository.deleteAll();
        dialogRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void getDialogs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs")
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1));
    }
}
