package com.example.springboot3jwtauthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
  @Email(message = "Email should be valid")
  @NotBlank(message = "Email is required")
  String email;
  @NotBlank(message = "Password is required")
  String password;
}
