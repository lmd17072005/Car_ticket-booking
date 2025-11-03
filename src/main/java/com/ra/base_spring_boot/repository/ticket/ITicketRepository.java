package com.ra.base_spring_boot.repository.ticket;

import com.ra.base_spring_boot.model.Bus.Ticket;
import com.ra.base_spring_boot.model.constants.TicketStatus;
import com.ra.base_spring_boot.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import java.util.Set;

@Repository
public interface ITicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByScheduleIdAndSeatId(Long scheduleId, Long seatId);

    List<Ticket> findByUser(User user);

    Optional<Ticket> findByIdAndUser_Phone(Long id, String phone);

    List<Ticket> findByUserAndStatus(User user, TicketStatus status);

    @Query("SELECT t.seat.id FROM Ticket t WHERE t.schedule.id = :scheduleId AND t.status = 'BOOKED'")
    Set<Long> findBookedSeatIdsByScheduleId(Long scheduleId);

}
