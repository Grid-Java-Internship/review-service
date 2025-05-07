package com.internship.review_service.service;

import com.internship.review_service.dto.request.EditRequest;
import com.internship.review_service.dto.response.EntityRatingResponse;
import com.internship.review_service.feign.job.JobDTO;
import com.internship.review_service.feign.user.UserDTO;
import com.internship.review_service.dto.request.ReviewRequest;
import com.internship.review_service.dto.response.ReviewResponse;
import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;
import com.internship.review_service.exception.ConflictException;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.feign.job.JobService;
import com.internship.review_service.feign.user.UserService;
import com.internship.review_service.mapper.ReviewMapper;
import com.internship.review_service.model.Review;
import com.internship.review_service.rabbitmq.producer.ReviewMessageProducer;
import com.internship.review_service.repository.ReviewRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewMessageProducer reviewMessageProducer;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewRequest request;

    private ReviewResponse response;

    private Review review;

    private UserDTO userDTO;

    private JobDTO jobDTO;

    private EditRequest editRequest;

    @BeforeEach
    void beforeEach() {
        review = Review.builder()
                .id(1L)
                .userId(1L)
                .reviewedId(1L)
                .reviewType(ReviewType.JOB)
                .status(Status.ACCEPTED)
                .rating(10)
                .text("Text")
                .build();

        request = ReviewRequest.builder()
                .userId(1L)
                .reviewedId(1L)
                .reviewType(ReviewType.JOB)
                .rating(10)
                .text("Text")
                .build();

        response = ReviewResponse.builder()
                .id(1L)
                .userId(1L)
                .reviewedId(1L)
                .reviewType(ReviewType.JOB)
                .status(Status.ACCEPTED)
                .rating(10)
                .text("Text")
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .surname("surname")
                .build();

        jobDTO = JobDTO.builder()
                .id(1L)
                .userId(2L)
                .title("title")
                .description("description")
                .dateOfPosting(java.time.LocalDate.now())
                .experience(1)
                .hourlyRate(1)
                .build();

        editRequest = EditRequest.builder()
                .id(1L)
                .userId(1L)
                .text("Text edit")
                .rating(1)
                .build();
    }

    @Test
    void addReview_shouldReturnTrue_whenReviewIsAddedToDb() {

        when(userService.getUser(request.getUserId())).thenReturn(userDTO);
        when(jobService.getJobById(request.getReviewedId())).thenReturn(jobDTO);
        when(reviewMapper.toEntity(request)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        doNothing().when(reviewMessageProducer).sendAddedReviewMessage(anyString(), anyLong());

        boolean result = reviewService.addReview(request);

        assertTrue(result);
        verify(reviewRepository).save(review);
        verify(reviewMessageProducer).sendAddedReviewMessage(userDTO.getEmail(), request.getUserId());
    }

    @Test
    void addReview_shouldThrowException_whenUserNotFound() {

        when(userService.getUser(request.getUserId())).thenThrow(FeignException.NotFound.class);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> reviewService.addReview(request));

        assertEquals("User with this id not found! id: " + request.getUserId(), ex.getMessage());
    }

    @Test
    void addReview_shouldThrowException_whenUserCannotReviewThemselves() {

        request.setReviewedId(1L);
        request.setReviewType(ReviewType.USER);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> reviewService.addReview(request)
        );

        assertEquals("User cannot review themselves.", ex.getMessage());
    }

    @Test
    void addReview_shouldThrowException_whenJobNotFound() {

        when(userService.getUser(request.getUserId())).thenReturn(userDTO);
        when(jobService.getJobById(request.getReviewedId())).thenThrow(FeignException.NotFound.class);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> reviewService.addReview(request));

        assertEquals("Job with this id not found! id: " + request.getReviewedId(), ex.getMessage());
    }

    @Test
    void addReview_shouldThrowException_whenUserCannotReviewOwnJob() {

        jobDTO.setUserId(1L);

        when(userService.getUser(request.getUserId())).thenReturn(userDTO);
        when(jobService.getJobById(request.getReviewedId())).thenReturn(jobDTO);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> reviewService.addReview(request)
        );

        assertEquals("User cannot review their own job! id: " + request.getUserId(), ex.getMessage());
    }

    @Test
    void addReview_shouldThrowException_whenReviewedUserDoesntExist() {

        request.setReviewedId(2L);
        request.setReviewType(ReviewType.USER);

        when(userService.getUser(request.getUserId())).thenReturn(userDTO);
        when(userService.getUser(request.getReviewedId())).thenThrow(FeignException.NotFound.class);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> reviewService.addReview(request));

        assertEquals("User with this id not found! id: " + request.getUserId(), ex.getMessage());
    }

    @Test
    void deleteReview_shouldReturnTrue_whenReviewIsDeletedFromDb() {

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        boolean result = reviewService.deleteReview(1L, 1L);

        assertTrue(result);
        verify(reviewRepository).save(review);
    }

    @Test
    void deleteReview_shouldThrowException_whenReviewNotFound() {

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> reviewService.deleteReview(1L, 1L)
        );

        assertEquals("Review with this id not found! id: 1", ex.getMessage());
    }

    @Test
    void deleteReview_shouldThrowException_whenUserCannotDeleteAnotherUserReview() {

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> reviewService.deleteReview(2L, 1L)
        );

        assertEquals("User cannot delete another user's review!", ex.getMessage());
    }

    @Test
    void getReview_shouldReturnReview_whenReviewIsFound() {

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        ReviewResponse result = reviewService.getReview(1L);

        assertEquals(reviewMapper.toDto(review), result);
    }

    @Test
    void getReview_shouldThrowException_whenReviewNotFound() {

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> reviewService.getReview(1L)
        );

        assertEquals("Review with this id not found! id: 1", ex.getMessage());
    }

    @Test
    void getAllReviews_shouldReturnReviews_whenReviewsAreFound() {

        when(reviewRepository.findByReviewedIdAndStatusAndReviewType(anyLong(), any(Status.class), any(ReviewType.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));
        when(reviewMapper.toDto(review)).thenReturn(response);

        List<ReviewResponse> result = reviewService.getAllReviews(ReviewType.USER, 1L, 0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void editReview_shouldEditReview_whenReviewIsFoundAndBelongsToUser() {

        response.setRating(editRequest.getRating());
        response.setText(editRequest.getText());

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewMapper.toDto(review)).thenReturn(response);

        ReviewResponse result = reviewService.editReview(editRequest);

        assertNotNull(result);
        assertEquals(editRequest.getText(), result.getText());
        assertEquals(editRequest.getRating(), result.getRating());
    }

    @Test
    void editReview_shouldThrowException_whenReviewNotFound() {

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> reviewService.editReview(editRequest)
        );

        assertEquals("Review with this id not found! id: 1", ex.getMessage());
    }

    @Test
    void editReview_shouldThrowException_whenUserCannotEditAnotherUserReview() {

        editRequest.setUserId(10L);

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> reviewService.editReview(editRequest)
        );

        assertEquals("User cannot edit another user's review!", ex.getMessage());
    }

    @Test
    void getEntityRating_shouldReturnAverageRating() {

        when(reviewRepository.findByReviewedIdAndReviewTypeAndStatus(
                anyLong(),
                any(ReviewType.class),
                any(Status.class)
        )).thenReturn(List.of(review));

        EntityRatingResponse result = reviewService.getEntityRating(1L, ReviewType.JOB);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ReviewType.JOB, result.getReviewType());
        assertEquals(10, result.getRating());
        assertEquals(1, result.getReviewCount());
    }

    @Test
    void getEntityRating_shouldHandleEmptyList() {

        when(reviewRepository.findByReviewedIdAndReviewTypeAndStatus(
                anyLong(),
                any(ReviewType.class),
                any(Status.class)
        )).thenReturn(Collections.emptyList());

        EntityRatingResponse result = reviewService.getEntityRating(1L, ReviewType.JOB);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ReviewType.JOB, result.getReviewType());
        assertEquals(0, result.getRating());
        assertEquals(0, result.getReviewCount());
    }

    @Test
    void getUserLeftReviews_shouldReturnReviews_whenUserHasLeftReviews() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        List<Review> reviews = List.of(review);
        PageImpl<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

        when(reviewRepository.findByUserIdAndStatus(userId, Status.ACCEPTED, pageable))
                .thenReturn(reviewPage);
        when(reviewMapper.toDto(review)).thenReturn(response);

        // Act
        List<ReviewResponse> result = reviewService.getUserLeftReviews(userId, page);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(reviewRepository).findByUserIdAndStatus(userId, Status.ACCEPTED, pageable);
        verify(reviewMapper).toDto(review);
    }

    @Test
    void getUserLeftReviews_shouldReturnEmptyList_whenUserHasNoReviews() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);

        when(reviewRepository.findByUserIdAndStatus(userId, Status.ACCEPTED, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        List<ReviewResponse> result = reviewService.getUserLeftReviews(userId, page);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository).findByUserIdAndStatus(userId, Status.ACCEPTED, pageable);
        verify(reviewMapper, never()).toDto(any());
    }

    @Test
    void getUserLeftReviews_shouldUseCorrectPagination() {
        // Arrange
        Long userId = 1L;
        int page = 2;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        when(reviewRepository.findByUserIdAndStatus(userId, Status.ACCEPTED, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        List<ReviewResponse> result = reviewService.getUserLeftReviews(userId, page);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).findByUserIdAndStatus(userId, Status.ACCEPTED, pageable);
    }
}