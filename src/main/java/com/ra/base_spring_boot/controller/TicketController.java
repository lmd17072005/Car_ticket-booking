package com.ra.base_spring_boot.controller;


import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.ticket.TicketRequest;
import com.ra.base_spring_boot.dto.ticket.TicketResponse;
import com.ra.base_spring_boot.services.ticket.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ITicketService ticketService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseWrapper<TicketResponse>> bookTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        return new ResponseEntity<>(ResponseWrapper.<TicketResponse>builder().
                status(HttpStatus.CREATED)
                .data(ticketService.bookTicket(ticketRequest))
                .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my-tickets")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseWrapper<List<TicketResponse>>> getMyTickets() {
        return ResponseEntity.ok(
                ResponseWrapper.<List<TicketResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(ticketService.getMyTickets())
                        .build()
        );
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseWrapper<String>> cancelTicket(@PathVariable("id") Long ticketId) {
        ticketService.cancelTicket(ticketId);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Ticket cancelled successfully")
                        .build()
        );
    }
}
