package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.request.dialogs.LincRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.dialogs.*;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface DialogsService {
    ListDataResponse<DialogsData> getDialogs(String query, int offset, int itemPerPage, Principal principal);

    DataResponse<DialogsData> createDialog(DialogRequestForCreate userId, Principal principal) throws PersonNotFoundException;

    DataResponse<CountContent> getUnreadedDialogs(Principal principal);

    DataResponse<DialogsData> deleteDialog(int id);

    DataResponse<DialogPersonIdContent> putPersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal);

    DataResponse<DialogPersonIdContent> deletePersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal);

    DataResponse<LinkContent> inviteLink(int id, Principal principal);

    DataResponse<DialogPersonIdContent> joinInLink(int id, LincRequest lincRequest, Principal principal);

    ListDataResponse<MessageData> getMessagesById(int id, String query, int offset, int itemPerPage, int fromMessageId, Principal principal);
}
