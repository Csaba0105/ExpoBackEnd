package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.models.messages.Message;
import com.example.springboot3jwtauthentication.repositories.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public void sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        messagingTemplate.convertAndSend("/topic/chat/" + message.getReceiver(), message);
    }

    @GetMapping("/{sender}/{receiver}")
    public List<Message> getMessages(@PathVariable String sender, @PathVariable String receiver) {
        return messageRepository.findBySenderAndReceiver(sender, receiver);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message handleWebSocketMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        return message;
    }
}
