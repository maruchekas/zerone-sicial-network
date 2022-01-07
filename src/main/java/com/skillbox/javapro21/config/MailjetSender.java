package com.skillbox.javapro21.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.skillbox.javapro21.config.properties.MailjetRegistrationParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Service
@AllArgsConstructor
public class MailjetSender {
    private final MailjetRegistrationParam mailjet;

    public void send(String emailTo, String message) throws MailjetException, IOException {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        String messageResponse = null;
        String[] split = message.split("/");
        if (Arrays.asList(split).contains("register")) {
            File htmlMessageFile = new File("./src/main/resources/messages/register.html");
            String htmlString = FileUtils.readFileToString(htmlMessageFile);
            htmlString = htmlString.replace("$message", message);
            messageResponse = htmlString;
        } else if (Arrays.asList(split).contains("recovery")) {
            File htmlMessageFile = new File("./src/main/resources/messages/recovery.html");
            String htmlString = FileUtils.readFileToString(htmlMessageFile);
            htmlString = htmlString.replace("$message", message);
            messageResponse = htmlString;
        } else messageResponse = message;
        client = new MailjetClient(ClientOptions.builder().apiKey(mailjet.getKey()).apiSecretKey(mailjet.getSecret()).build());
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", mailjet.getFrom())
                                        .put("Name", "Administrator Zerone. Neo. Anderson Neo."))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", emailTo)
                                                .put("Name", "Здравствуй, дорогой")))
                                .put(Emailv31.Message.SUBJECT, "Здравствуй, дорогой")
                                .put(Emailv31.Message.TEXTPART, "Тебя приветствует команда проекта Zerone")
                                .put(Emailv31.Message.HTMLPART, messageResponse)
                                .put(Emailv31.Message.CUSTOMID, "AppGettingStartedTest")));
        response = client.post(request);
        log.info(String.valueOf(response.getStatus()));
        log.info(String.valueOf(response.getData()));
    }
}

