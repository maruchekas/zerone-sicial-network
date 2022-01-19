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
import com.skillbox.javapro21.domain.enumeration.ReadStatus;
import com.skillbox.javapro21.exception.MessageNotFoundException;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.exception.UserExistOnDialogException;
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

    @Override
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

    @Override
    public DataResponse<DialogContent> createDialog(DialogRequestForCreate dialogRequest, Principal principal) throws PersonNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        List<Person> personList = personRepository.findAllById(dialogRequest.getUsersIds());
        Optional<Person> personDst = personList.stream().findFirst();
        if (personList.size() == 0) throw new PersonNotFoundException("Пользователи не найдены");
        if (personList.size() == 1) {
            List<Dialog> dialogsListByPersonId = dialogRepository.findDialogsListByPersonId(person.getId());
            if (!dialogsListByPersonId.isEmpty()) {
                List<Dialog> dialogs = dialogsListByPersonId.stream()
                        .map(d -> {
                            if (d.getPersons().stream().anyMatch(p -> p.getId().equals(personDst.get().getId()))
                                    && d.getPersons().stream().anyMatch(p -> p.getId().equals(person.getId()))
                                    && d.getPersons().size() == 2) {
                                return d;
                            } else {
                                return null;
                            }
                        }).toList();
                if (dialogs.get(0) != null) {
                    return new DataResponse<DialogContent>()
                            .setError("")
                            .setTimestamp(utilsService.getTimestamp())
                            .setData(new DialogContent().setId(dialogs.get(0).getId()));
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
                            .setTimestamp(utilsService.getTimestamp())
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
                        .setTimestamp(utilsService.getTimestamp())
                        .setData(new DialogContent().setId(savedDialog.getId()));
            }
        }
        return null;
    }

    @Override
    public DataResponse<CountContent> getUnreadedDialogs(Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        List<PersonToDialog> dialogs = personToDialogRepository.findDialogsByPersonId(person.getId());
        int count = 0;
        for (PersonToDialog p2d : dialogs) {
            count += dialogRepository.findDialogById(p2d.getDialogId()).orElseThrow().getMessages().stream()
                    .filter(message -> {
                        if (!message.getAuthor().getId().equals(person.getId())) {
                            return message.getReadStatus().equals(SENT);
                        }
                        return false;
                    }).count();
        }
        return new DataResponse<CountContent>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(new CountContent().setCount(count));
    }

    @Override
    public DataResponse<DialogContent> deleteDialog(int id) {
        Dialog dialog = dialogRepository.findDialogById(id).orElseThrow();
        dialog.setIsBlocked(2);
        Dialog save = dialogRepository.save(dialog);
        return getDataResponseWithId(save.getId());
    }

    @Override
    public DataResponse<DialogPersonIdContent> putPersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal) {
        utilsService.findPersonByEmail(principal.getName());
        List<Person> personList = personRepository.findAllById(listPersons.getUsersIds());
        Dialog dialog = dialogRepository.findDialogById(id).orElseThrow();
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

    @Override
    public DataResponse<DialogPersonIdContent> deletePersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal) {
        utilsService.findPersonByEmail(principal.getName());
        List<Person> personList = personRepository.findAllById(listPersons.getUsersIds());
        Dialog dialog = dialogRepository.findDialogById(id).orElseThrow();
        for (Person p : personList) {
            dialog.getPersons().remove(p);
        }
        if (dialog.getPersons().size() == 1) {
            dialog.setIsBlocked(2);
        }
        dialogRepository.save(dialog);
        return getDataResponseWithListPersonsId(listPersons.getUsersIds());
    }

    @Override
    public DataResponse<LinkContent> inviteLink(int id, Principal principal) {
        String token = utilsService.getToken();
        Dialog dialog = dialogRepository.findDialogById(id).orElseThrow();
        dialog.setCode(token);
        dialogRepository.save(dialog);
        return new DataResponse<LinkContent>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(new LinkContent()
                        .setLink(token));
    }

    @Override
    public DataResponse<DialogPersonIdContent> joinInLink(int id, LincRequest lincRequest, Principal principal) throws UserExistOnDialogException {
        Dialog dialog = dialogRepository.findByCode(lincRequest.getLink());
        Person person = utilsService.findPersonByEmail(principal.getName());
        if (!dialog.getPersons().stream().anyMatch(p -> p.getId().equals(person.getId()))) {
            Set<Person> personSet = new HashSet<>();
            personSet.add(person);
            dialog
                    .setCode("")
                    .setPersons(personSet);
            Dialog sDialog = dialogRepository.save(dialog);
            PersonToDialog personToDialog = new PersonToDialog();
            personToDialog
                    .setDialogId(sDialog.getId())
                    .setPersonId(person.getId())
                    .setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
            personToDialogRepository.save(personToDialog);
            List<Long> list = new ArrayList<>();
            for (Person p : personSet) {
                list.add(p.getId());
            }
            return getDataResponseWithListPersonsId(list);
        } else {
            throw new UserExistOnDialogException("Пользователь уже в есть в диалоге");
        }
    }

    @Override
    public ListDataResponse<MessageContent> getMessagesById(int id, String query, int offset, int itemPerPage, Long fromMessageId, Principal principal) throws MessageNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(person.getId(), id);
        p2d.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d);
        Page<Message> messages;
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        if (fromMessageId == -1) {
            if (query.equals("")) {
                messages = messageRepository.findByDialogIdAndPersonId(id, person.getId(), pageable);
            } else {
                messages = messageRepository.findByDialogIdAndPersonIdAndQuery(id, person.getId(), query.toLowerCase(Locale.ROOT), pageable);
            }
        } else {
            messageRepository.findById(fromMessageId).orElseThrow(() -> new MessageNotFoundException("Сообщения с данным id не существует"));
            if (query.equals("")) {
                messages = messageRepository.findByDialogIdAndPersonIdAndMessageId(id, person.getId(), fromMessageId, pageable);
            } else {
                messages = messageRepository.findByDialogIdAndPersonIdAndQueryAndMessageId(id, person.getId(), query.toLowerCase(Locale.ROOT), fromMessageId, pageable);
            }
        }
        return getListDataResponseWithMessage(offset, itemPerPage, messages);
    }

    @Override
    public DataResponse<MessageContent> postMessagesById(int id, MessageTextRequest messageText, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(person.getId(), id);
        p2d.setLastCheck(LocalDateTime.now(ZoneOffset.UTC));
        personToDialogRepository.save(p2d);
        Dialog dialog = dialogRepository.findDialogById(id).orElseThrow();
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

    @Override
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
                .setTimestamp(utilsService.getTimestamp())
                .setData(new MessageIdContent().setMessageId(save.getId()));
    }

    @Override
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

    @Override
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

    @Override
    public DataResponse<MessageOkContent> readeMessage(int dialogId, Long messageId, Principal principal) {
        Message message = messageRepository.findByDialogIdMessageId(dialogId, messageId);
        message
                .setReadStatus(READ);
        messageRepository.save(message);
        return utilsService.getMessageOkResponse();
    }

    @Override
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
        }
        return new DataResponse<LastActivityContent>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(lastActivityContent);
    }

    @Override
    public DataResponse<MessageOkContent> postActivityPersonInDialog(int id, Long userId, Principal principal) throws PersonNotFoundException {
        PersonToDialog p2d = personToDialogRepository.findDialogByPersonIdAndDialogId(userId, id);
        if (p2d.getLastCheck().isAfter(LocalDateTime.now(ZoneOffset.UTC).minusSeconds(2))) {
            return utilsService.getMessageOkResponse();
        } else {
            throw new PersonNotFoundException("Пользователь не активен");
        }
    }

    private ListDataResponse<MessageContent> getListDataResponseWithMessage(int offset, int itemPerPage, Page<Message> messagePage) {
        return new ListDataResponse<MessageContent>()
                .setError("")
                .setOffset(offset)
                .setPerPage(itemPerPage)
                .setTimestamp(utilsService.getTimestamp())
                .setTotal((int) messagePage.getTotalElements())
                .setData(getMessageForResponse(messagePage.toList()));
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
                .setTimestamp(utilsService.getTimestamp())
                .setData(new DialogPersonIdContent().setUserIds(usersIds));
    }

    private DataResponse<DialogContent> getDataResponseWithId(int id) {
        return new DataResponse<DialogContent>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(new DialogContent().setId(id));
    }

    private DataResponse<MessageContent> getDataResponseWithMessageData(Message message, PersonToDialog p2d) {
        return new DataResponse<MessageContent>()
                .setError("")
                .setTimestamp(utilsService.getTimestamp())
                .setData(getMessageData(message, p2d));
    }


    private ListDataResponse<DialogContent> getListDataResponse(int offset, int itemPerPage, Page<PersonToDialog> allMessagesByPersonIdAndQuery) {
        return new ListDataResponse<DialogContent>()
                .setError("")
                .setOffset(offset)
                .setPerPage(itemPerPage)
                .setTimestamp(utilsService.getTimestamp())
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
        ReadStatus readStatus = message.getTime().isBefore(personToDialog.getLastCheck()) ? SENT : READ;
        if (readStatus.equals(READ) && message.getReadStatus().equals(SENT)) {
            message.setReadStatus(READ);
            messageRepository.save(message);
        }
        return new MessageContent()
                .setMessageText(message.getMessageText())
                .setAuthor(utilsService.getAuthData(message.getAuthor(), null))
                .setRecipient(utilsService.getAuthData(message.getRecipient(), null))
                .setId(message.getId())
                .setTime(message.getTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                .setReadStatus(readStatus);
    }
}
