package com.internship.review_service.mapper;

import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.model.JobReview;
import com.internship.review_service.model.UserReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewMapper {

    UserReview toEntity(ReviewCreateDto reviewCreateDto);

    JobReview toJobEntity(ReviewCreateDto reviewCreateDto);

    @Mapping(source = "userReviewId", target = "review_id")
    ReviewDto toDto(UserReview review);

    @Mapping(source = "jobReviewId", target = "review_id")
    ReviewDto toJobDto(JobReview review);

}
