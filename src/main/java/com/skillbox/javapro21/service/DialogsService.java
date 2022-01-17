package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.request.dialogs.LincRequest;
import com.skillbox.javapro21.api.request.dialogs.MessageTextRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.dialogs.*;
import com.skillbox.javapro21.exception.MessageNotFoundException;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.exception.UserExistOnDialogException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface DialogsService {
    ListDataResponse<DialogContent> getDialogs(String query, int offset, int itemPerPage, Principal principal);

    DataResponse<DialogContent> createDialog(DialogRequestForCreate userId, Principal principal) throws PersonNotFoundException;

    DataResponse<CountContent> getUnreadedDialogs(Principal principal);

    DataResponse<DialogContent> deleteDialog(int id);

    DataResponse<DialogPersonIdContent> putPersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal);

    DataResponse<DialogPersonIdContent> deletePersonsInDialog(int id, DialogRequestForCreate listPersons, Principal principal);

    DataResponse<LinkContent> inviteLink(int id, Principal principal);

    DataResponse<DialogPersonIdContent> joinInLink(int id, LincRequest lincRequest, Principal principal) throws UserExistOnDialogException;

    ListDataResponse<MessageContent> getMessagesById(int id, String query, int offset, int itemPerPage, Long fromMessageId, Principal principal) throws MessageNotFoundException;

    DataResponse<MessageContent> postMessagesById(int id, MessageTextRequest messageText, Principal principal);

    DataResponse<MessageIdContent> deleteMessageById(int dialogId, Long messageId, Principal principal);

    DataResponse<MessageContent> putMessageById(int dialogId, Long messageId, MessageTextRequest messageText, Principal principal);

    DataResponse<MessageContent> putRecoverMessageById(int dialogId, Long messageId, Principal principal) throws MessageNotFoundException;

    DataResponse<MessageOkContent> readeMessage(int dialogId, Long messageId, Principal principal);

    DataResponse<LastActivityContent> activityPersonInDialog(int id, Long userId, Principal principal);

    DataResponse<MessageOkContent> postActivityPersonInDialog(int id, Long userId, Principal principal) throws PersonNotFoundException;
}
