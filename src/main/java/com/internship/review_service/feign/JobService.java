package com.internship.review_service.feign;

import com.internship.review_service.dto.JobResponse;
import com.internship.review_service.feign.exceptionhandler.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "job-service", url = "${configs.feign.jobs}", configuration = FeignConfig.class)
public interface JobService {

    @GetMapping("/{id}")
    ResponseEntity<JobResponse> getJobById(@PathVariable Long id);
}
