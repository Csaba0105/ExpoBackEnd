package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.dto.UserDTO;

public interface UserSettingsService {
    UserDTO updateSettings(Long userId, UserDTO userDTO);
}
