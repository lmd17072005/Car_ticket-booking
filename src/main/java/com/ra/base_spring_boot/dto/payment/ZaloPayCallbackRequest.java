package com.ra.base_spring_boot.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ZaloPayCallbackRequest {
    @JsonProperty("data")
    private String data;

    @JsonProperty("mac")
    private String mac;
}