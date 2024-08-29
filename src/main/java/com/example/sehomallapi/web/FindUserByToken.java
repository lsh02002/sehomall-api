package com.example.sehomallapi.web;

import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FindUserByToken {
    private final UserRepository userRepository;

    public User findUser(CustomUserDetails customUserDetails) {
        User user = userRepository.findById(
                customUserDetails.getId())
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다.", customUserDetails.getNickname()));

        if(!user.getEmail().equals(customUserDetails.getUsername())) throw new NotFoundException("유저가 존재하지 않습니다.", customUserDetails.getNickname());
        return user;
    }
}
