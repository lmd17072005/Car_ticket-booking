package com.ra.base_spring_boot.dto.review;

import com.ra.base_spring_boot.model.Bus.BusReview;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private String userName; // Tên người đánh giá
    private Long busId;
    private int rating;
    private String review;
    private LocalDateTime createdAt;

    public ReviewResponse(BusReview busReview) {
        this.id = busReview.getId();
        this.busId = busReview.getBus().getId();
        this.rating = busReview.getRating();
        this.review = busReview.getReview();
        this.createdAt = busReview.getCreatedAt();
        if (busReview.getUser() != null) {
            this.userName = busReview.getUser().getFirstName() + " " + busReview.getUser().getLastName();
        }
    }
}