package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.review.ReviewRequest;
import com.ra.base_spring_boot.dto.review.ReviewResponse;
import com.ra.base_spring_boot.services.review.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Dùng base URL chung
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    /**
     * API công khai, lấy tất cả đánh giá của một xe bus cụ thể.
     */
    @GetMapping("/buses/{busId}/reviews")
    public ResponseEntity<ResponseWrapper<List<ReviewResponse>>> getReviewsByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseWrapper.<List<ReviewResponse>>builder()
                .status(HttpStatus.OK)
                .data(reviewService.getReviewsByBusId(busId))
                .build());
    }

    /**
     * API cho người dùng đã đăng nhập tạo một đánh giá mới.
     */
    @PostMapping("/reviews")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseWrapper<ReviewResponse>> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        return new ResponseEntity<>(ResponseWrapper.<ReviewResponse>builder()
                .status(HttpStatus.CREATED)
                .data(reviewService.createReview(reviewRequest))
                .build(), HttpStatus.CREATED);
    }

    /**
     * API cho phép chủ nhân của đánh giá hoặc Admin xóa một đánh giá.
     */
    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()") // Yêu cầu đăng nhập, logic chi tiết sẽ được xử lý trong service
    public ResponseEntity<ResponseWrapper<String>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .data("Xóa đánh giá thành công")
                .build());
    }
}