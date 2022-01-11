package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.platform.LanguageData;
import com.skillbox.javapro21.service.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    @Override
    public ListDataResponse<LanguageData> getLanguage(String language, int offset, int itemPerPage) {

        LanguageData languageData = new LanguageData();
        languageData.setId(1L);
        languageData.setTypeId(1L);
        languageData.setEntityId(1L);
        languageData.setSentTime(LocalDateTime.now());
        languageData.setInfo("Русский");

        ListDataResponse<LanguageData> languageDataListDataResponse = new ListDataResponse<>();
        languageDataListDataResponse.setError("ok");
        languageDataListDataResponse.setOffset(offset);
        languageDataListDataResponse.setPerPage(itemPerPage);
        languageDataListDataResponse.setTotal(1);
        languageDataListDataResponse.setTimestamp(LocalDateTime.now());
        languageDataListDataResponse.setData(List.of(languageData));

        return languageDataListDataResponse;
    }
}
