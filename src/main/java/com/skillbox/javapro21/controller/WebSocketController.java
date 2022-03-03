package com.skillbox.javapro21.controller;

import com.skillbox.javapro21.api.request.dialogs.MessageTextRequest;
import com.skillbox.javapro21.api.response.dialogs.CountContent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    @MessageMapping("/message")
    @SendToUser("/topic/messages")
    public String getMessage(final MessageTextRequest message) {
        return message.getMessageText();
    }

    @MessageMapping("/unreaded")
    @SendToUser("/topic/unreaded")
    public int getUnreaded(final CountContent countContent) {
        return countContent.getCount();
    }

    @MessageMapping("/notification")
    @SendToUser("/topic/notifications")
    public String getNotification() {
        return null;
    }

//    private final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
//    private final SimpMessagingTemplate messagingTemplate;
//    private final PersonRepository personRepository;
//    private final UtilsService utilsService;
//
//    int count = 0;
//    @PreAuthorize("hasAuthority('user:write')")
//    @PostMapping("/api/v1/messages/{id}")
//    public String sendMessage(@PathVariable final String id, @RequestBody final MessageTextRequest message, Principal principal) {
//        Person srcPerson = utilsService.findPersonByEmail(principal.getName());
//        Person dstPerson = personRepository.findPersonById(Long.parseLong(id)).get();
//        messagingTemplate.convertAndSendToUser(dstPerson.getEmail(), "/topic/messages", message);
//        count = count + 1;
//        messagingTemplate.convertAndSendToUser(dstPerson.getEmail(), "/topic/unreaded", count);
//
//        logger.info(srcPerson.getEmail() + " sent message to " + id);
//        return "ok";
//    }
}
