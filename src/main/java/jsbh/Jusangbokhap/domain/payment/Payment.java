package jsbh.Jusangbokhap.domain.payment;

import jakarta.persistence.*;
import lombok.*;

import jsbh.Jusangbokhap.domain.user.User;
import jsbh.Jusangbokhap.domain.reservation.Reservation;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    private Integer price;
    private String paymentMethod;
    private String paymentStatus;
}
