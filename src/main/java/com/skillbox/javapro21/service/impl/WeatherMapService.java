package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.platform.WeatherResponse;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.exception.UnauthorizedUserException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;

import static com.skillbox.javapro21.config.Constants.*;

@Component
@RequiredArgsConstructor
public class WeatherMapService {

    @Value("${weather.api.key}")
    private String key;

    private final UtilsService utilsService;

    public WeatherResponse showWeather(Principal principal) throws UnauthorizedUserException, IOException, ParseException {
        if (principal == null) {
            throw new UnauthorizedUserException();
        }

        WeatherResponse weatherResponse = new WeatherResponse();
        Person person = utilsService.findPersonByEmail(principal.getName());
        String cityName = StringUtils.isEmpty(person.getTown()) ? "Москва" : person.getTown();

        JSONObject jsonObject = getWeatherJsonObject(cityName);

        JSONObject main = (JSONObject) jsonObject.get("main");
        JSONArray weatherData = (JSONArray) jsonObject.get("weather");
        JSONObject weatherToday = (JSONObject) weatherData.get(0);

        int temp = (int) Math.round(Double.parseDouble(main.get("temp").toString()));
        String description = weatherToday.get("description").toString();
        String icon = String.format(WEATHER_ICON_URL,
                weatherToday.get("icon"));

        String formattedTemp = temp < 0 ? String.valueOf(temp) : "+" + temp;

        weatherResponse
                .setCity(cityName)
                .setTemp(formattedTemp)
                .setForecastIcon(icon)
                .setDescription(description);

        return weatherResponse;
    }

    private JSONObject getWeatherJsonObject(String cityName) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        URL url = new URL(String.format(WEATHER_MAP_URL, cityName, key));
        URLConnection conn = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        return (JSONObject) jsonParser.parse(reader);
    }

}
