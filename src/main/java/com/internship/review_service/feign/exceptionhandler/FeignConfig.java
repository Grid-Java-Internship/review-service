package com.internship.review_service.feign.exceptionhandler;

import com.internship.review_service.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 404) {
                return new NotFoundException("User not found.");
            }
            return new Exception("Something snapped!" + response.status());
        }
    }
}
