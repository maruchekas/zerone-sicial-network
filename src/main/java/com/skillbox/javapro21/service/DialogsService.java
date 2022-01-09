package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.request.dialogs.DialogRequestForCreate;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.dialogs.DialogsData;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface DialogsService {
    ListDataResponse<DialogsData> getDialogs(String query, int offset, int itemPerPage, Principal principal);

    DataResponse<DialogsData> createDialog(DialogRequestForCreate userId, Principal principal) throws PersonNotFoundException;
}
