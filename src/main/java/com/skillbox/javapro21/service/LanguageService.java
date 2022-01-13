package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.platform.LanguageData;
import org.springframework.stereotype.Service;

@Service
public interface LanguageService {

    ListDataResponse<LanguageData> getLanguage(String language, int offset, int itemPerPage);
}
