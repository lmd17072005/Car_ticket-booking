package com.ra.base_spring_boot.services.ticket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra.base_spring_boot.dto.payment.CreateOrderRequest;
import com.ra.base_spring_boot.dto.ticket.TicketLookupRequest;
import com.ra.base_spring_boot.dto.ticket.TicketRequest;
import com.ra.base_spring_boot.dto.ticket.TicketResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpForbiden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.Bus.Seat;
import com.ra.base_spring_boot.model.Bus.Ticket;
import com.ra.base_spring_boot.model.constants.PaymentMethod;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.model.constants.TicketStatus;
import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import com.ra.base_spring_boot.model.payment.Payment;
import com.ra.base_spring_boot.model.user.User;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.bus.ISeatRepository;
import com.ra.base_spring_boot.dto.ticket.CancelTicketRequest;
import com.ra.base_spring_boot.repository.payment.ICancellationPolicyRepository;
import com.ra.base_spring_boot.repository.payment.IPaymentRepository;
import com.ra.base_spring_boot.repository.schedule.IScheduleRepository;
import com.ra.base_spring_boot.repository.ticket.ITicketRepository;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {
    private final ITicketRepository ticketRepository;
    private final IScheduleRepository scheduleRepository;
    private final ISeatRepository seatRepository;
    private final IUserRepository userRepository;
    private final ICancellationPolicyRepository cancellationPolicyRepository;
    private final IPaymentRepository paymentRepository;
    private final IPaymentService paymentService;

    @Override
    @Transactional
    public String initiateBooking(TicketRequest ticketRequest, HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new HttpNotFound("User not found"));
        Schedule schedule = scheduleRepository.findById(ticketRequest.getScheduleId()).orElseThrow(() -> new HttpNotFound("Schedule not found"));
        Seat seat = seatRepository.findById(ticketRequest.getSeatId()).orElseThrow(() -> new HttpNotFound("Seat not found"));

        if (schedule.getAvailableSeats() <= 0) { throw new HttpConflict("The trip is sold out."); }
        if (ticketRepository.existsByScheduleIdAndSeatId(schedule.getId(), seat.getId())) { throw new HttpConflict("Seat is already booked."); }

        BigDecimal finalPrice = schedule.getRoute().getPrice().add(seat.getPriceForSeatType());
        String orderInfo = String.format("{\"scheduleId\": %d, \"seatId\": %d}", ticketRequest.getScheduleId(), ticketRequest.getSeatId());

        Payment newPayment = new Payment();
        newPayment.setUser(currentUser);
        newPayment.setAmount(finalPrice);
        newPayment.setStatus(PaymentStatus.PENDING);
        newPayment.setOrderInfo(orderInfo);
        newPayment.setPaymentMethod(PaymentMethod.ONLINE);
        newPayment.setTicket(null);
        Payment savedPayment = paymentRepository.save(newPayment);

        String description = "Thanh toan ve xe cho " + currentUser.getEmail();
        return paymentService.createVnPayOrder(savedPayment.getId(), finalPrice, description, request);
    }


    @Override
    @Transactional
    public TicketResponse finalizeTicketCreation(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpNotFound("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) { throw new HttpBadRequest("Payment is not completed."); }
        if (payment.getTicket() != null) { throw new HttpConflict("A ticket has already been created for this payment."); }

        Long scheduleId, seatId;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Long> orderData = objectMapper.readValue(payment.getOrderInfo(), new TypeReference<>() {});
            scheduleId = orderData.get("scheduleId");
            seatId = orderData.get("seatId");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse order info from payment: " + e.getMessage());
        }

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new HttpNotFound("Schedule not found"));
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new HttpNotFound("Seat not found"));

        schedule.setAvailableSeats(schedule.getAvailableSeats() - 1);
        if (schedule.getAvailableSeats() == 0) {
            schedule.setStatus(ScheduleStatus.FULL);
        }
        scheduleRepository.save(schedule);

        Ticket newTicket = new Ticket();
        newTicket.setUser(payment.getUser());
        newTicket.setSchedule(schedule);
        newTicket.setSeat(seat);
        newTicket.setDepartureTime(schedule.getDepartureTime());
        newTicket.setArrivalTime(schedule.getArrivalTime());
        newTicket.setSeatType(seat.getSeatType());
        newTicket.setPrice(payment.getAmount());
        newTicket.setStatus(TicketStatus.BOOKED);
        Ticket savedTicket = ticketRepository.save(newTicket);

        payment.setTicket(savedTicket);
        paymentRepository.save(payment);

        return new TicketResponse(savedTicket);
    }

    @Override
    @Transactional
    public void cancelTicket(Long ticketId, CancelTicketRequest cancelRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new HttpNotFound("User not found"));
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new HttpNotFound("Ticket not found with id : " + ticketId));

        if (!ticket.getUser().getId().equals(currentUser.getId())) {
            throw new HttpForbiden("User is not authorized to cancel this ticket.");
        }
        if (ticket.getStatus() != TicketStatus.BOOKED) {
            throw new HttpConflict("Only booked tickets can be cancelled.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = ticket.getDepartureTime();
        if (now.isAfter(departureTime)) {
            throw new HttpBadRequest("Cannot cancel ticket after departure time.");
        }
        long minutesUntilDeparture = Duration.between(now, departureTime).toMinutes();

        int refundPercentage;
        if (minutesUntilDeparture > 1440) {
            refundPercentage = 100;
        } else if (minutesUntilDeparture > 720) {
            refundPercentage = 70;
        } else {
            refundPercentage = 0;
        }

        ticket.setStatus(TicketStatus.CANCELLED);

        Schedule schedule = ticket.getSchedule();
        schedule.setAvailableSeats(schedule.getAvailableSeats() + 1);
        if (schedule.getStatus() == ScheduleStatus.FULL) {
            schedule.setStatus(ScheduleStatus.UPCOMING);
        }

        CancellationPolicy cancellationLog = new CancellationPolicy();
        cancellationLog.setDescriptions(cancelRequest.getDescriptions());
        cancellationLog.setRoute(ticket.getSchedule().getRoute());
        cancellationLog.setCancellationTimeLimit((int) minutesUntilDeparture);
        cancellationLog.setRefundPercentage(refundPercentage);

        cancellationPolicyRepository.save(cancellationLog);
        scheduleRepository.save(schedule);
        ticketRepository.save(ticket);

        if (refundPercentage > 0) {
            BigDecimal refundAmount = ticket.getPrice().multiply(BigDecimal.valueOf(refundPercentage / 100.0));
            System.out.println("Yêu cầu hoàn tiền: " + refundAmount + " cho vé ID: " + ticket.getId());
        }
    }

    @Override
    public List<TicketResponse> getMyTickets(TicketStatus status) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new HttpNotFound("User not found"));

        List<Ticket> tickets;
        if (status != null) {
            tickets = ticketRepository.findByUserAndStatus(currentUser, status);
        } else {
            tickets = ticketRepository.findByUser(currentUser);
        }

        return tickets.stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponse lookupTicket(TicketLookupRequest request) {
        Ticket ticket = ticketRepository.findByTicketCodeAndUser_Phone(request.getTicketCode(), request.getPhone())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy vé phù hợp với thông tin đã cung cấp."));
        return new TicketResponse(ticket);
    }
}