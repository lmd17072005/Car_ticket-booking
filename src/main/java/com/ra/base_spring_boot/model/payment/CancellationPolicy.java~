package com.ra.base_spring_boot.model.payment;
import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import com.ra.base_spring_boot.model.Bus.Ticket;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.ra.base_spring_boot.model.Bus.Route;
import java.time.LocalDateTime;



@Entity
@Table(name = "cancellation_policies")
@Getter
@Setter
public class CancellationPolicy extends BaseObject {
@Column(name = "descriptions", nullable = false, columnDefinition = "TEXT")
private String descriptions;
@ManyToOne
@JoinColumn(name = "route_id")
private Route route;
@Column(name = "cancellation_time_limit", nullable = false)
private int cancellationTimeLimit;
@Column(name = "refund_percentage", nullable = false)
private int refundPercentage;
@CreationTimestamp
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;
@UpdateTimestamp
@Column(name = "updated_at", nullable = false)
private LocalDateTime updatedAt;
}