package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ticket.TicketResponse;
import com.ra.base_spring_boot.services.ticket.ITicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/tickets")
@RequiredArgsConstructor
public class InternalTicketController {

    private final ITicketService ticketService;

    @PostMapping("/finalize/{paymentId}")
    public ResponseEntity<TicketResponse> finalizeTicketCreation(@PathVariable Long paymentId) {
        return ResponseEntity.ok(ticketService.finalizeTicketCreation(paymentId));
    }
}
