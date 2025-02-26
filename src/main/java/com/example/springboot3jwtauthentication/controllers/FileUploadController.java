package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.services.MinioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final MinioService minioService;

    public FileUploadController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName, @RequestHeader("Authorization") String authToken) {
        try {
            //UserDTO user = userService.getUserProfile(authToken);
            //log.info("User profile retrieved successfully: {}", user.getId());
            //TODO Implement user authorization logic
            minioService.deleteFile(fileName);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File deletion failed: " + e.getMessage());
        }
    }
}
