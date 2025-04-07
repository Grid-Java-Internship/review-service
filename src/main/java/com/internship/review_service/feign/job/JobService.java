package com.internship.review_service.feign.job;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "job-service", url = "${microservicesUrls.job-service}")
public interface JobService {

    @GetMapping("/{id}")
    JobDTO getJobById(@PathVariable Long id);
}
