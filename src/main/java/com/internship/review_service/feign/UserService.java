package com.internship.review_service.feign;

import com.internship.review_service.dto.UserDto;
import com.internship.review_service.feign.exceptionhandler.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",url = "${configs.feign.client}", configuration = FeignConfig.class)
public interface UserService {

    @GetMapping("/{id}")
    ResponseEntity<UserDto> getUser(@PathVariable Long id);

}
