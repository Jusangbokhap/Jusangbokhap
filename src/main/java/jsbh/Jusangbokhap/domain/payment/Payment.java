package jsbh.Jusangbokhap.domain.payment;

import jakarta.persistence.*;
import jsbh.Jusangbokhap.api.payment.dto.PayApproveResponseDto;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.user.User;
import lombok.*;

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

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false, unique = true)
    private String tid;

    public void updatePaymentOnSuccess(PayApproveResponseDto responseDto) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.price = responseDto.getAmount().getTotal();
        this.paymentMethod = responseDto.getPayment_method_type();
    }

    public void updatePaymentOnFailure() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void validateBeforeCancel() {
        if (this.paymentStatus == PaymentStatus.CANCELED) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }
    }

    public void cancel() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }
}
