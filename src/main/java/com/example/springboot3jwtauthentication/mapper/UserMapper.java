package com.example.springboot3jwtauthentication.mapper;

import com.example.springboot3jwtauthentication.dto.UserDTO;
import com.example.springboot3jwtauthentication.models.User;

public class UserMapper {

        public static UserDTO toDTO(User user) {
            return UserDTO.builder()
                    .id(user.getId())
                    .userSortName(user.getUserSortName())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .imageUrl(user.getImageUrl())
                    .language(user.getSettings() != null ? user.getSettings().getLanguage() : "en")
                    .theme(user.getSettings() != null ? user.getSettings().getTheme() : "light")
                    .notificationsEnabled(user.getSettings() != null && user.getSettings().isNotificationsEnabled())
                    .build();
        }


}
