package com.ra.base_spring_boot.services.review;

import com.ra.base_spring_boot.dto.review.ReviewRequest;
import com.ra.base_spring_boot.dto.review.ReviewResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpForbiden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusReview;
import com.ra.base_spring_boot.model.Bus.Ticket;
import com.ra.base_spring_boot.model.constants.TicketStatus;
import com.ra.base_spring_boot.model.user.User;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.repository.review.IBusReviewRepository;
import com.ra.base_spring_boot.repository.ticket.ITicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final IBusReviewRepository busReviewRepository;
    private final ITicketRepository ticketRepository;
    private final IBusRepository busRepository;
    private final IUserRepository userRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy người dùng"));

        Ticket ticket = ticketRepository.findById(reviewRequest.getTicketId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy vé với ID: " + reviewRequest.getTicketId()));


        if (!ticket.getUser().getId().equals(currentUser.getId())) {
            throw new HttpForbiden("Bạn không có quyền đánh giá cho vé này.");
        }

        if (LocalDateTime.now().isBefore(ticket.getDepartureTime())) {
            throw new HttpBadRequest("Bạn chỉ có thể đánh giá sau khi chuyến đi đã khởi hành.");
        }

        Bus bus = ticket.getSchedule().getBus();
        if(busReviewRepository.existsByUserAndBus(currentUser, bus)){
            throw new HttpConflict("Bạn đã đánh giá cho xe này trước đây.");
        }

        BusReview newReview = new BusReview();
        newReview.setUser(currentUser);
        newReview.setBus(bus);
        newReview.setRating(reviewRequest.getRating().byteValue());
        newReview.setReview(reviewRequest.getReview());

        return new ReviewResponse(busReviewRepository.save(newReview));
    }

    @Override
    public void deleteReview(Long reviewId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy người dùng"));

        BusReview review = busReviewRepository.findById(reviewId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy đánh giá với ID: " + reviewId));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().name().equals("ROLE_ADMIN"));

        if (!review.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new HttpForbiden("Bạn không có quyền xóa đánh giá này.");
        }

        busReviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByBusId(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy xe bus với ID: " + busId));

        return busReviewRepository.findByBus(bus).stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }
}