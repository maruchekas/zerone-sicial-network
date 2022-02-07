package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.StringListDataResponse;
import com.skillbox.javapro21.config.Constants;
import com.skillbox.javapro21.service.LocationService;
import com.skillbox.javapro21.service.xmlparser.CitiesHandler;
import com.skillbox.javapro21.service.xmlparser.CountriesHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class LocationServiceImpl implements LocationService {

    private final UtilsService utilsService;

    @Value("${location}")
    private String LOCATION;
    private SAXParser parser;
    private Map<String, String> countries;

    @PostConstruct
    private void init() throws IOException, SAXException, ParserConfigurationException {
        parser = SAXParserFactory.newInstance().newSAXParser();
        CountriesHandler countriesHandler = new CountriesHandler();
        parser.parse(LOCATION, countriesHandler);
        countries = countriesHandler.getResult();
    }


    public StringListDataResponse getCountries() throws IOException, SAXException {
        List<String> dataList = countries.keySet().stream().toList();
        return utilsService.getStringListDataResponse(dataList);
    }

    @Override
    public StringListDataResponse getCities(String country) throws IOException, SAXException {
        String countryId = countries.get(country);
        if (countryId == null) {
            return utilsService.getStringListDataResponse(new ArrayList<>());
        }
        CitiesHandler citiesHandler = new CitiesHandler(countryId);
        parser.parse(LOCATION, citiesHandler);
        List<String> dataList = citiesHandler.getResult();
        return utilsService.getStringListDataResponse(dataList);
    }
}
