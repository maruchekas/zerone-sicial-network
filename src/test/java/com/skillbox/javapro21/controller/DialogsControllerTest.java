package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.AbstractTest;
import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.request.dialogs.LincRequest;
import com.skillbox.javapro21.api.request.dialogs.MessageTextRequest;
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
import org.junit.jupiter.api.Assertions;
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
import java.time.ZoneOffset;
import java.util.*;

import static com.skillbox.javapro21.domain.enumeration.ReadStatus.READ;
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

    private Person sP1;
    private Person sP2;
    private Person sP3;
    private Dialog sDialog;
    private Message sMessage;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        super.setup();
        String emailForOne = "test999@test.ru";
        String emailForTwo = "test1000@test.ru";
        String emailForThree = "test1001@test.ru";
        String password = "1234";
        String firstName = "Arcadiy";
        String lastName = "Parovozov";
        LocalDateTime reg_date = LocalDateTime.now(ZoneOffset.UTC).minusDays(2);

        Person person1 = new Person()
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
        sP1 = personRepository.save(person1);
        Person person2 = new Person()
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
        sP2 = personRepository.save(person2);
        Person person3 = new Person()
                .setEmail(emailForThree)
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRegDate(reg_date)
                .setMessagesPermission(MessagesPermission.ALL)
                .setIsBlocked(0)
                .setIsApproved(1)
                .setLastOnlineTime(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .setConfirmationCode("");
        sP3 = personRepository.save(person3);

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
        sMessage = messageRepository.save(message);
        Set<Message> messageSet = new HashSet<>();
        messageSet.add(sMessage);
        sDialog.setMessages(messageSet);
        this.sDialog = dialogRepository.save(sDialog);

        PersonToDialog p2d1 = personToDialogRepository.findDialogByPersonIdAndDialogId(sP1.getId(), sDialog.getId());
        p2d1.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d1);

        PersonToDialog p2d2 = personToDialogRepository.findDialogByPersonIdAndDialogId(sP2.getId(), sDialog.getId());
        p2d2.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d2);
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs?query=123")
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(0)).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs?query=t")
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs?query=w")
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(0));
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void createDialog() throws Exception {
        List<Long> personId = new ArrayList<>();
        personId.add(sP3.getId());
        DialogRequestForCreate dialogRequestForCreate = new DialogRequestForCreate();
        dialogRequestForCreate.setUsersIds(personId);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dialogs")
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dialogRequestForCreate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        personId.add(sP2.getId());
        dialogRequestForCreate.setUsersIds(personId);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dialogs")
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dialogRequestForCreate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void createDialogIfDialogAreCreated() throws Exception {
        List<Long> personId = new ArrayList<>();
        personId.add(sP2.getId());
        DialogRequestForCreate dialogRequestForCreate = new DialogRequestForCreate();
        dialogRequestForCreate.setUsersIds(personId);
        List<Dialog> dialogByAuthorAndRecipient = dialogRepository.findDialogsListByPersonId(sP1.getId());
        Optional<Dialog> dialogs = dialogByAuthorAndRecipient.stream()
                .map(d -> {
                    if (d.getPersons().stream().anyMatch(p -> p.getId().equals(sP2.getId()))
                            && d.getPersons().stream().anyMatch(p -> p.getId().equals(sP1.getId()))
                            && d.getPersons().size() == 2) {
                        return d;
                    } else {
                        return null;
                    }
                }).findFirst();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dialogs")
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dialogRequestForCreate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(dialogs.get().getId())).andReturn();
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void getUnreadedMessagesForP1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/unreaded")
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count").value(0)).andReturn();
    }

    @Test
    @WithMockUser(username = "test1000@test.ru", authorities = "user:write")
    void getUnreadedMessagesForP2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/unreaded")
                        .principal(() -> "test1000@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count").value(1)).andReturn();
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void deleteDialog() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/dialogs/{id}", sDialog.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(sDialog.getId())).andReturn();

        Assertions.assertEquals(Optional.empty(), dialogRepository.findDialogById(sDialog.getId()));
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void putPersonsInDialog() throws Exception {
        DialogRequestForCreate dialogRequestForCreate = new DialogRequestForCreate();
        List<Long> longList = new ArrayList<>();
        longList.add(sP3.getId());
        dialogRequestForCreate.setUsersIds(longList);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/dialogs/{id}/users", sDialog.getId())
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dialogRequestForCreate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void deletePersonsInDialog() throws Exception {
        DialogRequestForCreate dialogRequestForCreate = new DialogRequestForCreate();
        List<Long> longList = new ArrayList<>();
        longList.add(sP2.getId());
        dialogRequestForCreate.setUsersIds(longList);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/dialogs/{id}/users", sDialog.getId())
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dialogRequestForCreate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void inviteLinkAndJoinInLink() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/{id}/users/invite", sDialog.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.link")
                        .value(dialogRepository.findDialogById(sDialog.getId()).get().getCode())).andReturn();

        Optional<Dialog> dialog = dialogRepository.findDialogById(sDialog.getId());
        LincRequest lincRequest = new LincRequest();
        lincRequest.setLink(dialog.get().getCode());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/dialogs/{id}/users/join", sDialog.getId())
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(lincRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void getMessagesByIdDialog() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/{id}/messages", sDialog.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1)).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/{id}/messages?query=22222", sDialog.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(0)).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/{id}/messages?fromMessageId=221", sDialog.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/{id}/messages", sDialog.getId())
                        .param("fromMessageId", sMessage.getId().toString())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void postMessageById() throws Exception {
        MessageTextRequest messageTextRequest = new MessageTextRequest();
        messageTextRequest.setMessageText("NONONO!");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dialogs/{id}/messages", sDialog.getId())
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(messageTextRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void deleteMessageById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/dialogs/{dialog_id}/messages/{message_id}", sDialog.getId(), sMessage.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void putMessageById() throws Exception {
        MessageTextRequest messageTextRequest = new MessageTextRequest();
        messageTextRequest.setMessageText("NONONO!");
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/dialogs/{dialog_id}/messages/{message_id}", sDialog.getId(), sMessage.getId())
                        .principal(() -> "test999@test.ru")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(messageTextRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void putRecoverMessageById() throws Exception {
        Optional<Message> message = messageRepository.findById(sMessage.getId());
        message.get().setIsBlocked(2);
        Message save = messageRepository.save(message.get());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/dialogs/{dialog_id}/messages/{message_id}/recover", sDialog.getId(), save.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void readeMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/dialogs/{dialog_id}/messages/{message_id}/read", sDialog.getId(), sMessage.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Optional<Message> message = messageRepository.findById(sMessage.getId());
        Assertions.assertEquals(READ, message.get().getReadStatus());

    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void activityPersonInDialog() throws Exception {
        Optional<Person> person = personRepository.findByEmail(sP2.getEmail());
        person.get().setLastOnlineTime(LocalDateTime.now(ZoneOffset.UTC));
        Person save = personRepository.save(person.get());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs/{id}/activity/{user_id}", sDialog.getId(), save.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.online")
                        .value(true));
    }

    @Test
    @WithMockUser(username = "test999@test.ru", authorities = "user:write")
    void postActivityPersonInDialog() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dialogs/{id}/activity/{user_id}", sDialog.getId(), sP2.getId())
                        .principal(() -> "test999@test.ru"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message")
                        .value("ok"));
    }
}
