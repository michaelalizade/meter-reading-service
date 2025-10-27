package com.utilread.meterreading.service;

import com.utilread.meterreading.model.User;
import com.utilread.meterreading.repository.UserRepository;
import com.utilread.meterreading.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(UserPrincipal principal){
        Optional<User> existingUser = userRepository.findByKeycloakSub(principal.getKeycloakSub());

        if(existingUser.isPresent()){
            User user = existingUser.get();
            boolean updated = false;
            if(!user.getEmail().equals(principal.getEmail())){
                user.setEmail(principal.getEmail());
                updated = true;
            }
            if (!user.getFullName().equals(principal.getFullName())){
                user.setFullName(principal.getFullName());
                updated = true;
            }

            if(updated){
                log.debug("Updating user info for: {}", principal.getEmail());
                user = userRepository.save(user);
            }
            return user;
        }

        log.info("Creating new user from Keycloak: {}", principal.getEmail());
        User newUser = User.builder()
                .keycloakSub(principal.getKeycloakSub())
                .email(principal.getEmail())
                .fullName(principal.getFullName())
                .build();

        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        newUser = userRepository.saveAndFlush(newUser);
        log.info("Created user with ID: {}", newUser.getId());

        return userRepository.save(newUser);
    }

    // TODO: Add option to have multiple users

}
