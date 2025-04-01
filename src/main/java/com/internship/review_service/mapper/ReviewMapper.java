package com.internship.review_service.mapper;

import com.internship.review_service.dto.request.ReviewRequest;
import com.internship.review_service.dto.response.ReviewResponse;
import com.internship.review_service.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewMapper {

    Review toEntity(ReviewRequest dto);

    ReviewResponse toDto(Review entity);

}
