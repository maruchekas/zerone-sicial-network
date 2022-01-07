package com.skillbox.javapro21.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.skillbox.javapro21.config.properties.MailjetRegistrationParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailjetSender {
    private final MailjetRegistrationParam mailjet;
    private String htmlTextRegister;
    private String htmlTextRecovery;

    public void send(String emailTo, String message) throws MailjetException, IOException {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        String messageResponse = null;
        String[] split = message.split("/");
        if (Arrays.asList(split).contains("register")) {
            messageResponse = htmlTextRegister.replace("$message", message);
        } else if (Arrays.asList(split).contains("recovery")) {
            messageResponse = htmlTextRecovery.replace("$message", message);
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

    @PostConstruct
    private void getFileByPathRegister() {
        File htmlMessageFile = new File("./src/main/resources/messages/register.html");
        try {
            htmlTextRegister = FileUtils.readFileToString(htmlMessageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void getFileByPathRecovery() {
        File htmlMessageFile = new File("./src/main/resources/messages/recovery.html");
        try {
            htmlTextRecovery = FileUtils.readFileToString(htmlMessageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

