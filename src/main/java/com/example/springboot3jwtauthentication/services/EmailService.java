package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.dto.EmailDTO;

public interface EmailService {
    EmailDTO sendEmail(EmailDTO emailDTO);
}
