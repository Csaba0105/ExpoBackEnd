package com.example.springboot3jwtauthentication.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/message")
//public class MessageController {
//
//    private final SimpMessagingTemplate template;
//
//    //@MessageMapping("/message")
//    //public void createPrivateChatMessages(@RequestBody @Valid MessageCreateBindingModel messageCreateBindingModel, Principal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
//
//    }
//}
