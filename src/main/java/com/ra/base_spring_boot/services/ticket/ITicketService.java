package com.ra.base_spring_boot.services.ticket;

import com.ra.base_spring_boot.dto.ticket.TicketLookupRequest;
import com.ra.base_spring_boot.dto.ticket.TicketRequest;
import com.ra.base_spring_boot.dto.ticket.TicketResponse;
import com.ra.base_spring_boot.model.constants.TicketStatus;

import java.util.List;

public interface ITicketService {
    TicketResponse bookTicket(TicketRequest ticketRequest);

    List<TicketResponse> getMyTickets(TicketStatus status);

    void cancelTicket(Long ticketId);

    TicketResponse lookupTicket(TicketLookupRequest request);



}
