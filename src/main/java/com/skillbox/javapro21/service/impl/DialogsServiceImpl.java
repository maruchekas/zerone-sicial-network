package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.dialogs.DialogsData;
import com.skillbox.javapro21.api.response.dialogs.MessageData;
import com.skillbox.javapro21.domain.Message;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.MessageRepository;
import com.skillbox.javapro21.service.DialogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DialogsServiceImpl implements DialogsService {
    private final MessageRepository messageRepository;
    private final UtilsService utilsService;

    public ListDataResponse<DialogsData> getDialogs(String query, int offset, int itemPerPage, Principal principal) {
        Person person = utilsService.findPersonByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Page<Message> allMessagesByPersonIdAndQuery = messageRepository.findAllMessagesByPersonIdAndQuery(person.getId(), query, pageable);
        return getListDataResponse(offset, itemPerPage, allMessagesByPersonIdAndQuery);
    }

    private ListDataResponse<DialogsData> getListDataResponse(int offset, int itemPerPage, Page<Message> allMessagesByPersonIdAndQuery) {
        return new ListDataResponse<DialogsData>()
                .setError("")
                .setOffset(offset)
                .setPerPage(itemPerPage)
                .setTimestamp(LocalDateTime.now())
                .setTotal((int) allMessagesByPersonIdAndQuery.getTotalElements())
                .setData(getDialogsForResponse(allMessagesByPersonIdAndQuery));
    }

    private List<DialogsData> getDialogsForResponse(Page<Message> allMessagesByPersonIdAndQuery) {
        List<DialogsData> dialogsDataList = new ArrayList<>();
        allMessagesByPersonIdAndQuery.forEach(d -> {
            DialogsData data = getDialogData(d);
            dialogsDataList.add(data);
        });
        return dialogsDataList;
    }

    private DialogsData getDialogData(Message d) {
        return new DialogsData()
                .setId(d.getAuthor().getId())
                .setUnreadCount(0)
                .setLastMessage(
                        new MessageData()
                                .setId(d.getId())
                                .setTime(d.getTime())
                                .setAuthorId(d.getAuthor().getId())
                                .setRecipientId(d.getRecipient().getId())
                                .setMessageText(d.getMessageText())
                                .setReadStatus(d.getReadStatus())
                );
    }
}
