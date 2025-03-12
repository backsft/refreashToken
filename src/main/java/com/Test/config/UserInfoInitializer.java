package com.Test.config;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Test.entity.UserInfo;
import com.Test.repository.UserInfoRepository;

@Component
public class UserInfoInitializer implements CommandLineRunner {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInfoInitializer(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    //Create Super admin when run this application for the first time
    //Super admin will create more users and other important components of this software

    @Override
    public void run(String... args) {
        Optional<UserInfo> existingUser = userInfoRepository.findByName("superadmin");

        if (existingUser.isEmpty()) {
            UserInfo superAdmin = new UserInfo();
            superAdmin.setName("superadmin");
            superAdmin.setEmail("superadmin@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("123")); // Securely encrypt password
            superAdmin.setRoles("ROLE_SUPERADMIN");
            superAdmin.setEnabled(true);

            userInfoRepository.save(superAdmin);
            System.out.println("SuperAdmin user created successfully.");
        } else {
            System.out.println("SuperAdmin user already exists.");
        }
    }
}
