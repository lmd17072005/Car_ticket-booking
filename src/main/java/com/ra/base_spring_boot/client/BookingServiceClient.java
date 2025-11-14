package com.ra.base_spring_boot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "booking-service", url =  "http://localhost:8081")
public interface BookingServiceClient {

    @PostMapping("/api/v1/internal/tickets/finalize/{paymentId}")
    void finalizeTicketCreation(@PathVariable("paymentId") Long paymentId);
}