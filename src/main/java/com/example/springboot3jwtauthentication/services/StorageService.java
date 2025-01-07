package com.example.springboot3jwtauthentication.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${minio.url}")
    private String endpoint;


    @Autowired
    private MinioClient minioClient;

    public String uploadFile(String bucketName, MultipartFile file) {
        try {
            // Generate a unique filename
            String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            // Upload the file to MinIO
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        io.minio.PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(filename)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            // Generate and return the file URL
            return String.format("%s/%s/%s", endpoint, bucketName, filename);
        } catch (MinioException e) {
            throw new RuntimeException("Error while uploading file to MinIO: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while uploading the file: " + e.getMessage(), e);
        }
    }
}
