package com.internship.review_service.feign.impl;

import com.internship.review_service.dto.JobResponse;
import com.internship.review_service.feign.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl {

    private final JobService jobService;

    public JobResponse getJob(Long jobId) {
        return jobService.getJobById(jobId).getBody();
    }

}
