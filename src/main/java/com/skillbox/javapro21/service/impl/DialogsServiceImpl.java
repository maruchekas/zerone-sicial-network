package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.dialogs.DialogsData;
import com.skillbox.javapro21.api.response.dialogs.MessageData;
import com.skillbox.javapro21.domain.Dialog;
import com.skillbox.javapro21.domain.Message;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.PersonToDialog;
import com.skillbox.javapro21.domain.enumeration.ReadStatus;
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
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DialogsServiceImpl implements DialogsService {
    private final PersonToDialogRepository personToDialogRepository;
    private final PersonRepository personRepository;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final UtilsService utilsService;

    public ListDataResponse<DialogsData> getDialogs(String query, int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<PersonToDialog> allMessagesByPersonIdAndQuery = personToDialogRepository.findDialogsByPersonIdAndQuery(person.getId(), query, pageable);
        return getListDataResponse(offset, itemPerPage, allMessagesByPersonIdAndQuery);
    }

    public DataResponse<DialogsData> createDialog(DialogRequestForCreate dialogRequest, Principal principal) throws PersonNotFoundException {
        Person person = utilsService.findPersonByEmail(principal.getName());
        List<Person> personList = personRepository.findAllById(dialogRequest.getUsersIds());
        if (personList.size() == 0) throw new PersonNotFoundException("Пользователи не найдены");
        if (personList.size() == 1) {
            Optional<Person> personDst = personList.stream().findFirst();
            Dialog dialogByAuthorAndRecipient = dialogRepository.findPersonToDialogByPersonDialog(person.getId(), personDst.get().getId());
            if  (dialogByAuthorAndRecipient != null) {
                return new DataResponse<DialogsData>()
                        .setError("")
                        .setTimestamp(LocalDateTime.now())
                        .setData(new DialogsData().setId(dialogByAuthorAndRecipient.getId()));
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
                        .setLastCheck(LocalDateTime.now())
                        .setDialog(dialog)
                        .setPerson(person);
                PersonToDialog person2ToDialog = new PersonToDialog()
                        .setLastCheck(LocalDateTime.now())
                        .setDialog(dialog)
                        .setPerson(personDst.get());
                personToDialogRepository.save(person1ToDialog);
                personToDialogRepository.save(person2ToDialog);
                return new DataResponse<DialogsData>()
                        .setError("")
                        .setTimestamp(LocalDateTime.now())
                        .setData(new DialogsData().setId(savedDialog.getId()));
            }
        } else {
            Set<Person> personSet = new HashSet<>(personList);
            Dialog dialog = new Dialog()
                    .setPersons(personSet)
                    .setTitle("New chat")
                    .setIsBlocked(0);
            Dialog savedDialog = dialogRepository.save(dialog);
            PersonToDialog creatorToDialog = new PersonToDialog()
                    .setLastCheck(LocalDateTime.now())
                    .setDialog(dialog)
                    .setPerson(person);
            personToDialogRepository.save(creatorToDialog);
            for (Person p : personList) {
                PersonToDialog personToDialog = new PersonToDialog()
                        .setLastCheck(LocalDateTime.now())
                        .setDialog(dialog)
                        .setPerson(p);
                personToDialogRepository.save(personToDialog);
            }
            return new DataResponse<DialogsData>()
                    .setError("")
                    .setTimestamp(LocalDateTime.now())
                    .setData(new DialogsData().setId(savedDialog.getId()));
        }
    }

    private ListDataResponse<DialogsData> getListDataResponse(int offset, int itemPerPage, Page<PersonToDialog> allMessagesByPersonIdAndQuery) {
        return new ListDataResponse<DialogsData>()
                .setError("")
                .setOffset(offset)
                .setPerPage(itemPerPage)
                .setTimestamp(LocalDateTime.now())
                .setTotal((int) allMessagesByPersonIdAndQuery.getTotalElements())
                .setData(getDialogsForResponse(allMessagesByPersonIdAndQuery.toList()));
    }

    private List<DialogsData> getDialogsForResponse(List<PersonToDialog> allMessagesByPersonIdAndQuery) {
        List<DialogsData> dialogsDataList = new ArrayList<>();
        allMessagesByPersonIdAndQuery.forEach(p2d -> {
            DialogsData data = getDialogData(p2d);
            dialogsDataList.add(data);
        });
        return dialogsDataList;
    }

    private DialogsData getDialogData(PersonToDialog p2d) {
        DialogsData data = new DialogsData()
                .setId(p2d.getDialog().getId())
                .setUnreadCount(p2d.getDialog().getMessages().stream()
                        .filter(message -> message.getReadStatus().equals(ReadStatus.SENT)).count());
        if (p2d.getDialog().getMessages().size() > 0) {
            data.setLastMessage(getMessageData(
                    p2d.getDialog().getMessages().stream().max(Comparator.comparing(Message::getId)).get(), p2d)
            );
        } else {
            data.setLastMessage(new MessageData());
        }
        return data;
    }

    private MessageData getMessageData(Message message, PersonToDialog personToDialog) {
        return new MessageData()
                .setMessageText(message.getMessageText())
                .setAuthorId(message.getAuthor().getId())
                .setId(message.getId())
                .setTime(message.getTime())
                .setReadStatus(message.getTime().isAfter(personToDialog.getLastCheck()) ? ReadStatus.SENT : ReadStatus.READ);
    }
}
