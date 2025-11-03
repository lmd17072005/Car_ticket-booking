package com.ra.base_spring_boot.services.review;
import com.ra.base_spring_boot.dto.review.ReviewRequest;
import com.ra.base_spring_boot.dto.review.ReviewResponse;
import java.util.List;

public interface IReviewService {
    ReviewResponse createReview(ReviewRequest reviewRequest);
    void deleteReview(Long reviewId);
    List<ReviewResponse> getReviewsByBusId(Long busId);
}