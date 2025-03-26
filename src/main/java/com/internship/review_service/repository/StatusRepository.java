package com.internship.review_service.repository;

import com.internship.review_service.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    Status findStatusByStatusId(Long id);


}
