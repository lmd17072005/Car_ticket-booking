package com.ra.base_spring_boot.services.ticket;

import com.ra.base_spring_boot.dto.ticket.TicketRequest;
import com.ra.base_spring_boot.dto.ticket.TicketResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpForbiden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.Bus.Seat;
import com.ra.base_spring_boot.model.Bus.Ticket;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.model.constants.TicketStatus;
import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import com.ra.base_spring_boot.model.user.User;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.bus.ISeatRepository;
import com.ra.base_spring_boot.repository.payment.ICancellationPolicyRepository;
import com.ra.base_spring_boot.repository.schedule.IScheduleRepository;
import com.ra.base_spring_boot.repository.ticket.ITicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {
    private final ITicketRepository ticketRepository;
    private final IScheduleRepository scheduleRepository;
    private final ISeatRepository seatRepository;
    private final IUserRepository userRepository;
    private final ICancellationPolicyRepository cancellationPolicyRepository;


    @Override
    @Transactional
    public TicketResponse bookTicket(TicketRequest ticketRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new HttpNotFound("User not found"));

        // Get information about Schedule and Seat
        Schedule schedule = scheduleRepository.findById(ticketRequest.getScheduleId()).orElseThrow(() -> new HttpNotFound("Schedule not found with id : " + ticketRequest.getScheduleId()));
        Seat seat = seatRepository.findById(ticketRequest.getSeatId()).orElseThrow(() -> new HttpNotFound("Seat not found with id : " + ticketRequest.getSeatId()));

        // Check if the schedule is active
        if (schedule.getStatus() != ScheduleStatus.AVAILABLE) {
            throw new HttpConflict("The trip is sold out or has been cancelled.");
        }

        if (schedule.getAvailableSeats() <= 0) {
            throw new HttpConflict("The trip is sold out.");
        }

        if (!seat.getBus().getId().equals(schedule.getBus().getId())) {
            throw new HttpBadRequest("Seat does not belong to the bus for this schedule.");
        }

        if (ticketRepository.existsByScheduleIdAndSeatId(schedule.getId(), seat.getId())) {
            throw new HttpConflict("Seat is already booked for this schedule.");
        }

        // Update schedule status
        schedule.setAvailableSeats(schedule.getAvailableSeats() - 1);
        if (schedule.getAvailableSeats() == 0 ) {
            schedule.setStatus(ScheduleStatus.FULL);
        }
        scheduleRepository.save(schedule);

        // Calculate final price
        BigDecimal finalPrice = schedule.getRoute().getPrice().add(seat.getPriceForSeatType());

        //Create new ticket
        Ticket newTicket = new Ticket();
        newTicket.setUser(currentUser);
        newTicket.setSchedule(schedule);
        newTicket.setSeat(seat);
        newTicket.setDepartureTime(schedule.getDepartureTime());
        newTicket.setArrivalTime(schedule.getArrivalTime());
        newTicket.setSeatType(seat.getSeatType());
        newTicket.setPrice(finalPrice);
        newTicket.setStatus(TicketStatus.BOOKED);

        return new TicketResponse(ticketRepository.save(newTicket));
    }

    @Override
    @Transactional
    public void cancelTicket(Long ticketId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new HttpNotFound("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new HttpNotFound("Ticket not found with id : " + ticketId));

        if (!ticket.getUser().getId().equals(currentUser.getId())) {
            throw new HttpForbiden("User is not authorized to cancel this ticket.");
        }

        if (ticket.getStatus() != TicketStatus.BOOKED) {
            throw new HttpConflict("Only booked tickets can be cancelled.");
        }

        Route ticketRoute = ticket.getSchedule().getRoute();
        CancellationPolicy applicablePolicy = cancellationPolicyRepository.findByRoute(ticketRoute)
                .orElse(cancellationPolicyRepository.findGeneralPolicy()
                .orElseThrow(() -> new HttpNotFound("No cancellation policy found.")));

        long cancellationLimitMinutes = applicablePolicy.getCancellationTimeLimit();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = ticket.getDepartureTime();

        if (now.isAfter(departureTime)) {
            throw new HttpBadRequest("Cannot cancel ticket after departure time.");
        }
        long minutesUntilDeparture = Duration.between(now, departureTime).toMinutes();

        if (minutesUntilDeparture < cancellationLimitMinutes) {
            throw new HttpBadRequest("Cannot cancel ticket within " + cancellationLimitMinutes + " minutes of departure.");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        Schedule schedule = ticket.getSchedule();
        schedule.setAvailableSeats(schedule.getAvailableSeats() + 1);
        if (schedule.getStatus() == ScheduleStatus.FULL) {
            schedule.setStatus(ScheduleStatus.AVAILABLE);
        }
        scheduleRepository.save(schedule);

        int refundPercentage = applicablePolicy.getRefundPercentage();
        if (refundPercentage > 0) {
            BigDecimal refundAmount = ticket.getPrice().multiply(BigDecimal.valueOf(refundPercentage / 100.0));
            System.out.println("Refund Amount: " + refundAmount + " to ID ticket: " + ticket.getId());
        }
    }

    @Override
    public List<TicketResponse> getMyTickets() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new HttpNotFound("User not found"));

        return ticketRepository.findByUser(currentUser).stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
    }
}
