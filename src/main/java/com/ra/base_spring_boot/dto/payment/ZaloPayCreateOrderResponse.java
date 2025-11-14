package com.ra.base_spring_boot.dto.payment;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZaloPayCreateOrderResponse {
    @JsonProperty("return_code")
    private int returnCode;
    @JsonProperty("return_message")
    private String returnMessage;
    @JsonProperty("order_url")
    private String orderUrl;
    @JsonProperty("zp_trans_token")
    private String zpTransToken;
}
