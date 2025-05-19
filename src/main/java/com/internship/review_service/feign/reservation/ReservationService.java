package com.internship.review_service.feign.reservation;

import com.internship.authentication_library.feign.interceptor.ReservationServiceFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(url = "${microservicesUrls.reservation-service}", name = "reservation-service", configuration = ReservationServiceFeignConfiguration.class)
public interface ReservationService {

    @GetMapping("v1/reservations/customer/{customerId}")
    ResponseEntity<List<GetReservationResponse>> getReservationsByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    );

}
