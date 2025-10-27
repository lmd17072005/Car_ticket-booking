package com.ra.base_spring_boot.services.ticket;

import com.ra.base_spring_boot.dto.ticket.TicketRequest;
import com.ra.base_spring_boot.dto.ticket.TicketResponse;
import java.util.List;

public interface ITicketService {
    TicketResponse bookTicket(TicketRequest ticketRequest);

    List<TicketResponse> getMyTickets();

    void cancelTicket(Long ticketId);
}
}
