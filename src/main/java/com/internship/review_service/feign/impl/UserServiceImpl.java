package com.internship.review_service.feign.impl;

import com.internship.review_service.dto.UserDto;
import com.internship.review_service.feign.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl{

    private final UserService userService;

    public UserDto checkUser(Long id){
        return userService.getUser(id).getBody();
    }
}
