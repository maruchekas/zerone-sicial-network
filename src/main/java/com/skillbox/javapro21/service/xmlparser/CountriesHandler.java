package com.skillbox.javapro21.service.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class CountriesHandler extends DefaultHandler {

    private static final String COUNTRY = "country";
    private static final String COUNTRY_ID = "country_id";
    private static final String NAME = "name";

    private final Map<String, String> result = new HashMap<>();
    private boolean isCountry = false;
    private boolean isCountryId = false;
    private boolean isName = false;
    private String countyName;
    private String countyId;


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals(COUNTRY)) {
            isCountry = true;
        } else if (qName.equals(COUNTRY_ID) && isCountry) {
            isCountryId = true;
        } else if (qName.equals(NAME) && isCountry) {
            isName = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isCountryId) {
            countyId = new String(ch, start, length);
            isCountryId = false;
        } else if (isName) {
            countyName = new String(ch, start, length);
            isName = false;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(COUNTRY)) {
            isCountry = false;
            result.put(countyName, countyId);
        }
    }

    public Map<String, String> getResult() {
        return result;
    }
}
