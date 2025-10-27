package com.ra.base_spring_boot.repository.ticket;

import com.ra.base_spring_boot.model.Bus.Ticket;
import com.ra.base_spring_boot.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ITicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByScheduleIdAndSeatId(Long scheduleId, Long seatId);

    List<Ticket> findByUser(User user);

}
