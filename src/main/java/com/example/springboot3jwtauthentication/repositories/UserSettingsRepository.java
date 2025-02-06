package com.example.springboot3jwtauthentication.repositories;

import com.example.springboot3jwtauthentication.models.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}
