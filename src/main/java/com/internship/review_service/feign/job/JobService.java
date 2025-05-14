package com.internship.review_service.feign.job;

import com.internship.authentication_library.feign.interceptor.JobServiceFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "job-service",
        url = "${microservicesUrls.job-service}",
        configuration = JobServiceFeignConfiguration.class
)
public interface JobService {

    @GetMapping("/v1/jobs/{id}")
    JobDTO getJobById(@PathVariable Long id);
}
