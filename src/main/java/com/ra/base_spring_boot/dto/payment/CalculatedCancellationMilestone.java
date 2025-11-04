package com.ra.base_spring_boot.dto.payment;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculatedCancellationMilestone {
    private String startTimeDescription;

    @JsonFormat(pattern = "HH:mm dd/MM/yyyy")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "HH:mm dd/MM/yyyy")
    private LocalDateTime deadline;

    private String deadlineDescription;

    private int cancellationFeePercentage;
}