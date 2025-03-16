package com.Test.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Test.ExceptionArea.EmailAlreadyExistsException;
import com.Test.dto.UserSignupRequest;
import com.Test.entity.UserInfo;
import com.Test.repository.UserInfoRepository;

@Service
public class UserService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    // Directory to store uploaded images
    private static final String UPLOAD_DIR = "upload/Images";

    public ResponseEntity<String> signup(UserSignupRequest signupRequest, MultipartFile profilePicture) throws IOException {
        // Check if email already exists
        if (userInfoRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the profile picture and get the file path
        String profilePicturePath = null;
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + profilePicture.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(profilePicture.getInputStream(), filePath);
            profilePicturePath = UPLOAD_DIR + "/" + fileName;
        }

        // Create and save the UserInfo entity
        UserInfo userInfo = new UserInfo();
        userInfo.setName(signupRequest.getName());
        userInfo.setEmail(signupRequest.getEmail());
        userInfo.setPassword(signupRequest.getPassword()); // In a real app, hash the password
        userInfo.setRole(signupRequest.getRole());
        userInfo.setDrivingLicenseNumber(signupRequest.getDrivingLicenseNumber());
        userInfo.setDrivingLicenseExpiryDate(signupRequest.getDrivingLicenseExpiryDate());
        userInfo.setCarModel(signupRequest.getCarModel());
        userInfo.setProfilePicturePath(profilePicturePath);
        userInfo.setEnabled(true); // Enabled by default

        userInfoRepository.save(userInfo);
        return ResponseEntity.ok("User registered successfully with ID: " + userInfo.getId());
    }
}