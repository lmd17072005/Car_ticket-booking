package com.ra.base_spring_boot.services.ticket;

import com.ra.base_spring_boot.dto.ticket.CancelTicketRequest;
import com.ra.base_spring_boot.dto.ticket.TicketLookupRequest;
import com.ra.base_spring_boot.dto.ticket.TicketRequest;
import com.ra.base_spring_boot.dto.ticket.TicketResponse;
import com.ra.base_spring_boot.model.constants.TicketStatus;

import java.util.List;

public interface ITicketService {
    String initiateBooking(TicketRequest ticketRequest);
    TicketResponse finalizeTicketCreation(Long paymentId);

    void cancelTicket(Long ticketId, CancelTicketRequest cancelRequest);
    List<TicketResponse> getMyTickets(TicketStatus status);
    TicketResponse lookupTicket(TicketLookupRequest request);
}
