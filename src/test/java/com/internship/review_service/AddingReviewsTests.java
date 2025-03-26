package com.internship.review_service;


import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.dto.UserDto;
import com.internship.review_service.exception.NoReviewsOnResource;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.exception.UnknownUserIdException;
import com.internship.review_service.feign.JobService;
import com.internship.review_service.feign.UserService;
import com.internship.review_service.mapper.ReviewMapper;
import com.internship.review_service.model.JobReview;
import com.internship.review_service.model.Status;
import com.internship.review_service.model.UserReview;
import com.internship.review_service.rabbitmq.producer.ReviewMessageProducer;
import com.internship.review_service.repository.JobReviewRepository;
import com.internship.review_service.repository.StatusRepository;
import com.internship.review_service.repository.UserReviewRepository;
import com.internship.review_service.service.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddingReviewsTests {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private UserReviewRepository userReviewRepository;

    @Mock
    private JobService jobService;

    @Mock
    private JobReviewRepository jobReviewRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private UserService userService;

    @Mock
    private ReviewMessageProducer reviewMessageProducer;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewCreateDto reviewCreateDto;

    private UserReview userReview;

    private JobReview jobReview;

    private ReviewDto reviewDto;

    private Status status;

    private Long userId;

    private Long jobId;

    private Long reviewId;

    private UserDto userDto;

    private ResponseEntity<UserDto> userDtoResponseEntity;

    @BeforeEach
    void setUp() {
        userId = 1L;
        jobId = 1L;
        reviewId = 1L;
        status = new Status();

        //TODO CHANGE THIS TO ENUM
        status.setStatusType("ACCEPTED");

        status.setStatusId(3L);

        reviewCreateDto = new ReviewCreateDto("Aaaa", 3, "aaaa");

        userReview = new UserReview(1L, status);

        userReview.setReviewerId(userId);

        jobReview = new JobReview(1L, 1L, status);

        jobReview.setReviewerId(userId);

        reviewDto = new ReviewDto(1L, null, 1L, "aaaa", "Aaaa");

        userDto = new UserDto(1L, "John", "Doe", "john.doe@gmail.com");

        userDtoResponseEntity = new ResponseEntity<>(
                userDto, null, HttpStatus.OK
        );
    }

    @Test
    void testAddUserReview_Success() {

        when(userService.getUser(userId)).thenReturn(userDtoResponseEntity);
        when(reviewMapper.toEntity(reviewCreateDto)).thenReturn(userReview);
        when(statusRepository.findStatusByStatusId(3L)).thenReturn(status);
        when(userReviewRepository.save(userReview)).thenReturn(userReview);
        when(reviewMapper.toDto(userReview)).thenReturn(reviewDto);

        ReviewDto result = reviewService.addUserReview(userReview.getReviewerId(), reviewCreateDto);

        assertNotNull(result);

        assertDoesNotThrow(() -> userService.getUser(userReview.getUserReviewId()));

        verify(userService, atLeast(1)).getUser(userReview.getReviewerId());
        verify(reviewMapper).toEntity(reviewCreateDto);
        verify(statusRepository).findStatusByStatusId(3L);
        verify(userReviewRepository).save(userReview);
        verify(reviewMapper).toDto(userReview);
    }

    @Test
    void testAddUserReview_UserNotFound() {
        doThrow(new UnknownUserIdException("User not found"))
                .when(userService).getUser(userId);

        assertThrows(UnknownUserIdException.class, () -> reviewService.addUserReview(userId, reviewCreateDto));

        verify(userService).getUser(userId);
        verifyNoInteractions(reviewMapper, statusRepository, userReviewRepository);
    }

    @Test
    void testAddJobReview_Success() {
        when(reviewMapper.toJobEntity(reviewCreateDto)).thenReturn(jobReview);

        assertDoesNotThrow(() -> userService.getUser(userReview.getUserReviewId()));

        assertDoesNotThrow(() -> jobService.getJobById(jobReview.getJobReviewId()));

        when(userService.getUser(userId)).thenReturn(userDtoResponseEntity);

        when(jobReviewRepository.save(jobReview)).thenReturn(jobReview);

        when(reviewMapper.toJobDto(jobReview)).thenReturn(reviewDto);

        ReviewDto result = reviewService.addJobReview(userId, jobId, reviewCreateDto);

        assertNotNull(result);
    }

    @Test
    void testAddJobReview_JobNotFound() {
        when(userService.getUser(userId)).thenReturn(userDtoResponseEntity);
        when(jobService.getJobById(jobId)).thenThrow(new NotFoundException("Job not found"));

        assertThrows(NotFoundException.class, () -> reviewService.addJobReview(userId, jobId, reviewCreateDto));
    }

    @Test
    void testAddJobReview_UserNotFound() {
        when(userService.getUser(userId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> reviewService.addJobReview(userId, jobId, reviewCreateDto));
    }

    @Test
    void testGettingUserReview_Success() {
        when(userReviewRepository.findById(userId)).thenReturn(Optional.ofNullable(userReview));

        assertDoesNotThrow(() -> reviewService.getUserReview(userReview.getUserReviewId()));
    }

    @Test
    void testGettingUserReview_UserNotFound() {
        when(userReviewRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.getUserReview(userId));
    }

    @Test
    void gettingJobReviews_Success() {
        when(jobReviewRepository.findAllByJobId(jobId)).thenReturn(List.of(new JobReview()));

        assertDoesNotThrow(() -> reviewService.getAllJobReviews(jobId));
    }

    @Test
    void gettingJobReviews_JobNotFound() {
        when(jobReviewRepository.findAllByJobId(jobId)).thenReturn(List.of());

        assertThrows(NoReviewsOnResource.class, () -> reviewService.getAllJobReviews(jobId));
    }

    @Test
    void deletingUserReview_Success() {
        when(userReviewRepository.findById(userId)).thenReturn(Optional.ofNullable(userReview));

        assertDoesNotThrow(() -> reviewService.deleteUserReview(userId, reviewId));
    }

    @Test
    void deletingUserReview_UserNotFound() {
        when(userReviewRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.deleteUserReview(userId, reviewId));
    }

    @Test
    void deletingUserReview_NotSameUserId() {
        when(userReviewRepository.findById(userId)).thenReturn(Optional.ofNullable(userReview));


        assertThrows(UnknownUserIdException.class, () -> reviewService.deleteUserReview(2L, reviewId));

    }

}
