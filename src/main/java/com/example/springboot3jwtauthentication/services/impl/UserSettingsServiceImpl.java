package com.example.springboot3jwtauthentication.services.impl;

import com.example.springboot3jwtauthentication.dto.UserDTO;
import com.example.springboot3jwtauthentication.mapper.UserMapper;
import com.example.springboot3jwtauthentication.models.user.User;
import com.example.springboot3jwtauthentication.models.user.UserSettings;
import com.example.springboot3jwtauthentication.repositories.UserRepository;
import com.example.springboot3jwtauthentication.repositories.UserSettingsRepository;
import com.example.springboot3jwtauthentication.services.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSettingsServiceImpl implements UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public UserDTO updateSettings(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        UserSettings settings = user.getSettings();
        if (settings == null) {
            settings = new UserSettings();
            settings.setUser(user);
        }

        settings.setLanguage(userDTO.getLanguage());
        settings.setTheme(userDTO.getTheme());
        settings.setNotificationsEnabled(userDTO.isNotificationsEnabled());

        userSettingsRepository.save(settings);

        return UserMapper.toDTO(user);
    }
}
