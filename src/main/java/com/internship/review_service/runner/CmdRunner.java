package com.internship.review_service.runner;

import com.internship.review_service.model.Status;
import com.internship.review_service.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CmdRunner implements CommandLineRunner {

    private final StatusRepository statusRepository;

    @Override
    public void run(String... args) throws Exception {
        Status s = new Status();
        s.setStatusType("APPROVED");

        Status s2 = new Status();
        s2.setStatusType("DECLINED");

        Status s3 = new Status();
        s3.setStatusType("PENDING");

        statusRepository.saveAll(List.of(s,s2,s3));




    }
}
