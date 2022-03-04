package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.StringListDataResponse;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.IOException;

@Service
public interface LocationService {
    StringListDataResponse getCountries() throws IOException, SAXException;

    StringListDataResponse getCities(String country) throws IOException, SAXException;
}
