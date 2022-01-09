package com.skillbox.javapro21.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.skillbox.javapro21.config.properties.MailjetRegistrationParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class MailjetSender {
    private final MailjetRegistrationParam mailjet;
    private final String register;
    private final String recovery;
    private String htmlTextRegister;
    private String htmlTextRecovery;

    public MailjetSender(MailjetRegistrationParam mailjet,
                         @Value(value = "${html.file.register}") String register,
                         @Value(value = "${html.file.recovery}") String recovery) {
        this.mailjet = mailjet;
        this.register = register;
        this.recovery = recovery;
    }

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
        File htmlMessageFileRegister = new File(register);
        File htmlMessageFileRecovery = new File(recovery);
        try {
            htmlTextRegister = FileUtils.readFileToString(htmlMessageFileRegister);
            htmlTextRecovery = FileUtils.readFileToString(htmlMessageFileRecovery);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

