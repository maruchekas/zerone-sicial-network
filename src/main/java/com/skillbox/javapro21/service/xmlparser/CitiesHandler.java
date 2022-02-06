package com.skillbox.javapro21.service.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class CitiesHandler extends DefaultHandler {

    private static final String COUNTRY_ID = "country_id";
    private static final String CITY = "city";
    private static final String NAME = "name";

    private final List<String> result = new ArrayList<>();
    private boolean isCity = false;
    private boolean isCountryId = false;
    private boolean isName = false;
    private boolean match = false;

    private final String countryId;

    public CitiesHandler(String countryId) {
        this.countryId = countryId;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals(CITY)) {
            isCity = true;
        } else if (qName.equals(COUNTRY_ID) && isCity) {
            isCountryId = true;
        } else if (qName.equals(NAME) && isCity) {
            isName = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isCountryId) {
            String parsedCountryId = new String(ch, start, length);
            if (parsedCountryId.equals(countryId)) {
                match = true;
            }
            isCountryId = false;
        } else if (isName && match) {
            result.add(new String(ch, start, length));
            match = false;
        }
        isName = false;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(CITY)) {
            isCity = false;
        }
    }

    public List<String> getResult() {
        return result;
    }
}
