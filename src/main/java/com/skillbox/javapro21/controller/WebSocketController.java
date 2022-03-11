package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.dialogs.MessageTextRequest;
import com.skillbox.javapro21.api.response.WSNotificationResponse;
import com.skillbox.javapro21.api.response.dialogs.CountContent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    @MessageMapping("/message")
    @SendToUser("/topic/messages")
    public MessageTextRequest getMessage(final MessageTextRequest messageTextRequest) {
        return messageTextRequest;
    }

    @MessageMapping("/unreaded")
    @SendToUser("/topic/unreaded")
    public CountContent getUnreaded(final CountContent countContent) {
        return countContent;
    }

    @MessageMapping("/notification")
    @SendToUser("/topic/notifications")
    public WSNotificationResponse getNotification(WSNotificationResponse wsNotificationResponse) {
        return wsNotificationResponse;
    }
}
