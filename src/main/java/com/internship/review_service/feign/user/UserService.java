package com.internship.review_service.feign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${microservicesUrls.user-service}")
public interface UserService {

    @GetMapping("/v1/users/{id}")
    UserDTO getUser(@PathVariable("id") Long id);

}
