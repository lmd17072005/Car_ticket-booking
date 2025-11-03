package com.ra.base_spring_boot.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    @NotNull(message = "Ticket ID cannot be null")
    private Long ticketId; // Người dùng sẽ đánh giá dựa trên một vé cụ thể họ đã đi

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be from 1 to 5")
    @Max(value = 5, message = "Rating must be from 1 to 5")
    private Integer rating;

    private String review; // Nội dung review có thể không bắt buộc
}