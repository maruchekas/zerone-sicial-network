package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.request.dialogs.LincRequest;
import com.skillbox.javapro21.api.request.dialogs.MessageTextRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.dialogs.*;
import com.skillbox.javapro21.domain.Dialog;
import com.skillbox.javapro21.domain.Message;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.PersonToDialog;
import com.skillbox.javapro21.exception.MessageNotFoundException;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.repository.DialogRepository;
import com.skillbox.javapro21.repository.MessageRepository;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PersonToDialogRepository;
import com.skillbox.javapro21.service.DialogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.skillbox.javapro21.domain.enumeration.ReadStatus.READ;
import static com.skillbox.javapro21.domain.enumeration.ReadStatus.SENT;

@Component
@RequiredArgsConstructor
public class DialogsServiceImpl implements DialogsService {
    private final PersonToDialogRepository personToDialogRepository;
    private final PersonRepository personRepository;
    private final DialogRepository dialogRepository;
    private final UtilsService utilsService;
    private final MessageRepository messageRepository;

    public ListDataResponse<DialogContent> getDialogs(String query, int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<PersonToDialog> allMessagesByPersonIdAndQuery;
        if (query.equals("")) {
            allMessagesByPersonIdAndQuery = personToDialogRepository.findDialogsByPerson(person.getId(), pageable);
        } else {
            allMessagesByPersonIdAndQuery = personToDialogRepository.findDialogsByPersonIdAndQuery(person.getId(), query, pageable);
        }
        return getListDataResponse(offset, itemPerPage, allMessagesByPersonIdAndQuery);
    }

    public DataResponse<DialogContent> createDialog(DialogRequestForCreate dialogRequest, Principal principal) throws PersonNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        List<Person> personList = personRepository.findAllById(dialogRequest.getUsersIds());
        if (personList.size() == 0) throw new PersonNotFoundException("Пользователи не найдены");
        if (personList.size() == 1) {
            Optional<Person> personDst = personList.stream().findFirst();
            Dialog dialogByAuthorAndRecipient = dialogRepository.findPersonToDialogByPersonDialog(person.getId(), personDst.get().getId());
            if (dialogByAuthorAndRecipient != null) {
                return new DataResponse<DialogContent>()
                        .setError("")
                        .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                        .setData(new DialogContent().setId(dialogByAuthorAndRecipient.getId()));
            } else {
                Set<Person> personSet = new HashSet<>();
                personSet.add(person);
                personSet.add(personDst.get());
                Dialog dialog = new Dialog()
                        .setPersons(personSet)
                        .setTitle(personDst.get().getFirstName())
                        .setIsBlocked(0);
                Dialog savedDialog = dialogRepository.save(dialog);
                PersonToDialog person1ToDialog = new PersonToDialog()
                        .setLastCheck(LocalDateTime.now(ZoneOffset.UTC))
                        .setDialogId(savedDialog.getId())
                        .setPersonId(person.getId());
                PersonToDialog person2ToDialog = new PersonToDialog()
                        .setLastCheck(LocalDateTime.now(ZoneOffset.UTC))
                        .setDialogId(savedDialog.getId())
                        .setPersonId(personDst.get().getId());
                personToDialogRepository.save(person1ToDialog);
                personToDialogRepository.save(person2ToDialog);
                return new DataResponse<DialogContent>()
                        .setError("")
                        .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                        .setData(new DialogContent().setId(savedDialog.getId()));
            }
        } else {
            Set<Person> personSet = new HashSet<>(personList);
            Dialog dialog = new Dialog()
                    .setPersons(personSet)
                    .setTitle("New chat with " + personList.stream().findFirst().get().getFirstName() + " and other.")
                    .setIsBlocked(0);
            Dialog savedDialog = dialogRepository.save(dialog);
            PersonToDialog creatorDialog = new PersonToDialog()
                    .setLastCheck(LocalDateTime.now(ZoneOffset.UTC))
                    .setDialogId(savedDialog.getId())
                    .setPersonId(person.getId());
            personToDialogRepository.save(creatorDialog);
            for (Person p : personList) {
                PersonToDialog personToDialog = new PersonToDialog()
                        .setLastCheck(LocalDateTime.now(ZoneOffset.UTC))
                        .setDialogId(savedDialog.getId())
                        .setPersonId(p.getId());
                personToDialogRepository.save(personToDialog);
            }
            return new DataResponse<DialogContent>()
                    .setError("")
                    .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                    .setData(new DialogContent().setId(savedDialog.getId()));
        }
    }

    public DataResponse<CountContent> getUnreadedDialogs(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        List<PersonToDialog> dialogs = personToDialogRepository.findDialogsByPersonId(person.getId());
        int count = 0;
        for (PersonToDialog p2d : dialogs) {
            dialogRepository.findById(p2d.getDialogId());
            count += dialogRepository.findById(p2d.getDialogId()).orElseThrow().getMessages().stream()
                    .filter(message -> {
                        if (!message.getAuthor().getId().equals(person.getId())) {
                            return message.getReadStatus().equals(SENT);
                        }
                        return false;
                    }).count();
        }
        return new DataResponse<CountContent>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(new CountContent().setCount(count));
    }

    public DataResponse<DialogContent> deleteDialog(int id) {
        Dialog dialog = dialogRepository.findById(id).orElseThrow();
        dialog.setIsBlocked(2);
        Dialog save = dialogRepository.save(dialog);
        return getDataResponseWithId(save.getId());
    }

    public DataResponse<DialogPersonIdContent> putPersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal) {
        utilsService.findPersonByEmail(principal.getName());
        List<Person> personList = personRepository.findAllById(listPersons.getUsersIds());
        Dialog dialog = dialogRepository.findById(id).orElseThrow();
        dialog.setPersons(new HashSet<>(personList));
        Dialog save = dialogRepository.save(dialog);
        for (Person p : personList) {
            PersonToDialog personToDialog = new PersonToDialog()
                    .setLastCheck(LocalDateTime.now(ZoneOffset.UTC))
                    .setDialogId(save.getId())
                    .setPersonId(p.getId());
            personToDialogRepository.save(personToDialog);
        }
        return getDataResponseWithListPersonsId(listPersons.getUsersIds());
    }

    public DataResponse<DialogPersonIdContent> deletePersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal) {
        utilsService.findPersonByEmail(principal.getName());
        List<Person> personList = personRepository.findAllById(listPersons.getUsersIds());
        Dialog dialog = dialogRepository.findById(id).orElseThrow();;
        for (Person p : personList) {
            dialog.getPersons().remove(p);
        }
        dialogRepository.save(dialog);
        return getDataResponseWithListPersonsId(listPersons.getUsersIds());
    }

    public DataResponse<LinkContent> inviteLink(int id, Principal principal) {
        String token = utilsService.getToken();
        Dialog dialog = dialogRepository.findById(id).orElseThrow();
        dialog.setCode(token);
        dialogRepository.save(dialog);
        return new DataResponse<LinkContent>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(new LinkContent()
                        .setLink(token));
    }

    public DataResponse<DialogPersonIdContent> joinInLink(int id, LincRequest lincRequest, Principal principal) {
        Dialog dialog = dialogRepository.findByCode(lincRequest.getLink());
        Person person = utilsService.findPersonByEmail(principal.getName());
        Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        dialog
                .setCode("")
                .setPersons(personSet);
        dialogRepository.save(dialog);
        PersonToDialog personToDialog = new PersonToDialog();
        personToDialog
                .setDialogId(dialog.getId())
                .setPersonId(person.getId())
                .setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(personToDialog);
        List<Long> list = new ArrayList<>();
        for (Person p : personSet) {
            list.add(p.getId());
        }
        return getDataResponseWithListPersonsId(list);
    }

    public ListDataResponse<MessageContent> getMessagesById(int id, String query, int offset, int itemPerPage, int fromMessageId, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(person.getId(), id);
        p2d.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d);
        Page<Message> personToDialogs;
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        if (fromMessageId == -1) {
            if (query.equals("")) {
                personToDialogs = messageRepository.findByDialogIdAndPersonId(id, person.getId(), pageable);
            } else {
                personToDialogs = messageRepository.findByDialogIdAndPersonIdAndQuery(id, person.getId(), query, pageable);
            }
        } else {
            if (query.equals("")) {
                personToDialogs = messageRepository.findByDialogIdAndPersonIdAndMessageId(id, person.getId(), fromMessageId, pageable);
            } else {
                personToDialogs = messageRepository.findByDialogIdAndPersonIdAndQueryAndMessageId(id, person.getId(), query, fromMessageId, pageable);
            }
        }
        return getListDataResponseWithMessage(offset, itemPerPage, personToDialogs);
    }

    public DataResponse<MessageContent> postMessagesById(int id, MessageTextRequest messageText, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(person.getId(), id);
        p2d.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d);
        Dialog dialog = dialogRepository.findById(id).orElseThrow();
        List<Person> allPersonsByDialogId = personRepository.findAllByDialogId(id);
        List<Person> personList = allPersonsByDialogId.stream().filter(p -> !p.getId().equals(person.getId())).toList();
        Message message = new Message()
                .setDialog(dialog)
                .setMessageText(messageText.getMessageText())
                .setAuthor(person)
                .setTime(LocalDateTime.now(ZoneOffset.UTC))
                .setReadStatus(SENT)
                .setIsBlocked(0);
        Message save = null;
        if (personList.size() > 1) {
            for (Person p : personList) {
                message.setRecipient(p);
                save = messageRepository.save(message);
            }
        } else {
            message.setRecipient(personList.stream().findFirst().orElseThrow());
            save = messageRepository.save(message);
        }
        PersonToDialog p2DByDialogAndMessage = personToDialogRepository.findP2DByDialogAndMessage(id, person.getId());
        return getDataResponseWithMessageData(save, p2DByDialogAndMessage);
    }

    public DataResponse<MessageIdContent> deleteMessageById(int dialogId, Long messageId, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(person.getId(), dialogId);
        p2d.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d);
        Message message = messageRepository.findByDialogIdMessageId(dialogId, messageId);
        message.setIsBlocked(2);
        Message save = messageRepository.save(message);
        return new DataResponse<MessageIdContent>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(new MessageIdContent().setMessageId(save.getId()));
    }

    public DataResponse<MessageContent> putMessageById(int dialogId, Long messageId, MessageTextRequest messageText, Principal principal) {
        Message message = messageRepository.findByDialogIdMessageId(dialogId, messageId);
        Person person = utilsService.findPersonByEmail(principal.getName());
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(person.getId(), dialogId);
        p2d.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d);
        message
                .setMessageText(messageText.getMessageText())
                .setReadStatus(SENT)
                .setTime(LocalDateTime.now(ZoneOffset.UTC));
        Message save = messageRepository.save(message);
        PersonToDialog p2DByDialogAndMessage = personToDialogRepository.findP2DByDialogAndMessage(dialogId, person.getId());
        return getDataResponseWithMessageData(save, p2DByDialogAndMessage);
    }

    private DataResponse<MessageContent> getDataResponseWithMessageData(Message message, PersonToDialog p2d) {
        return new DataResponse<MessageContent>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(getMessageData(message, p2d));
    }

    public DataResponse<MessageContent> putRecoverMessageById(int dialogId, Long messageId, Principal principal) throws MessageNotFoundException {
        Message message = messageRepository.findDeletedMessageByDialogIdMessageId(dialogId, messageId);
        Person person = utilsService.findPersonByEmail(principal.getName());
        Message save;
        if (message.getIsBlocked() == 2) {
            message
                    .setIsBlocked(0)
                    .setReadStatus(SENT)
                    .setTime(LocalDateTime.now(ZoneOffset.UTC));
            save = messageRepository.save(message);
        } else {
            throw new MessageNotFoundException("Сообщение не удаленное");
        }
        PersonToDialog p2DByDialogAndMessage = personToDialogRepository.findP2DByDialogAndMessage(dialogId, person.getId());
        return getDataResponseWithMessageData(save, p2DByDialogAndMessage);
    }

    public DataResponse<MessageOkContent> readeMessage(int dialogId, Long messageId, Principal principal) {
        Message message = messageRepository.findDeletedMessageByDialogIdMessageId(dialogId, messageId);
        message
                .setReadStatus(READ);
        messageRepository.save(message);
        return utilsService.getMessageOkResponse();
    }

    public DataResponse<LastActivityContent> activityPersonInDialog(int id, Long userId, Principal principal) {
        Person recipient = personRepository.findPersonById(userId).orElseThrow();
        LastActivityContent lastActivityContent = new LastActivityContent();
        if (recipient.getLastOnlineTime().isAfter(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(5))) {
            lastActivityContent
                    .setOnline(true)
                    .setLastActivity(recipient.getLastOnlineTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        } else {
            lastActivityContent
                    .setOnline(false)
                    .setLastActivity(recipient.getLastOnlineTime().toInstant(ZoneOffset.UTC).toEpochMilli());
            ;
        }
        return new DataResponse<LastActivityContent>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(lastActivityContent);
    }

    public DataResponse<MessageOkContent> postActivityPersonInDialog(int id, Long userId, Principal principal) throws PersonNotFoundException {
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(userId, id);
        if (p2d.getLastCheck().isAfter(LocalDateTime.now(ZoneOffset.UTC).minusSeconds(2))) {
            return utilsService.getMessageOkResponse();
        } else {
            throw new PersonNotFoundException("Пользователь не активен");
        }
    }

    private ListDataResponse<MessageContent> getListDataResponseWithMessage(int offset, int itemPerPage, Page<Message> personToDialogs) {
        return new ListDataResponse<MessageContent>()
                .setError("")
                .setOffset(offset)
                .setPerPage(itemPerPage)
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setTotal((int) personToDialogs.getTotalElements())
                .setData(getMessageForResponse(personToDialogs.toList()));
    }

    private List<MessageContent> getMessageForResponse(List<Message> messages) {
        List<MessageContent> messageContentList = new ArrayList<>();
        Person person = utilsService.findPersonByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        messages.forEach(m -> {
            PersonToDialog p2DByDialogAndMessage = personToDialogRepository.findP2DByDialogAndMessage(m.getDialog().getId(), person.getId());
            MessageContent data = getMessageData(m, p2DByDialogAndMessage);
            messageContentList.add(data);
        });
        return messageContentList;
    }

    private DataResponse<DialogPersonIdContent> getDataResponseWithListPersonsId(List<Long> usersIds) {
        return new DataResponse<DialogPersonIdContent>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(new DialogPersonIdContent().setUserIds(usersIds));
    }

    private DataResponse<DialogContent> getDataResponseWithId(int id) {
        return new DataResponse<DialogContent>()
                .setError("")
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setData(new DialogContent().setId(id));
    }


    private ListDataResponse<DialogContent> getListDataResponse(int offset, int itemPerPage, Page<PersonToDialog> allMessagesByPersonIdAndQuery) {
        return new ListDataResponse<DialogContent>()
                .setError("")
                .setOffset(offset)
                .setPerPage(itemPerPage)
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setTotal((int) allMessagesByPersonIdAndQuery.getTotalElements())
                .setData(getDialogsForResponse(allMessagesByPersonIdAndQuery.toList()));
    }

    private List<DialogContent> getDialogsForResponse(List<PersonToDialog> allMessagesByPersonIdAndQuery) {
        List<DialogContent> dialogContentList = new ArrayList<>();
        allMessagesByPersonIdAndQuery.forEach(p2d -> {
            DialogContent data = getDialogData(p2d);
            dialogContentList.add(data);
        });
        return dialogContentList;
    }

    private DialogContent getDialogData(PersonToDialog p2d) {
        DialogContent data = new DialogContent();
        Dialog dialog = dialogRepository.findById(p2d.getDialogId()).orElseThrow();
        if (dialog.getMessages().size() > 0) {
            data
                    .setId(dialog.getId())
                    .setUnreadCount(dialog.getMessages().stream()
                            .filter(message -> message.getReadStatus().equals(SENT)).count());
            data.setLastMessage(getMessageData(
                    dialog.getMessages().stream().max(Comparator.comparing(Message::getId)).get(), p2d));
        } else {
            data.setLastMessage(new MessageContent());
        }
        return data;
    }

    private MessageContent getMessageData(Message message, PersonToDialog personToDialog) {
        return new MessageContent()
                .setMessageText(message.getMessageText())
                .setAuthor(utilsService.getAuthData(message.getAuthor(), null))
                .setRecipientId(utilsService.getAuthData(message.getRecipient(), null))
                .setId(message.getId())
                .setTime(message.getTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setReadStatus(message.getTime().isAfter(personToDialog.getLastCheck()) ? SENT : READ);
    }
}
