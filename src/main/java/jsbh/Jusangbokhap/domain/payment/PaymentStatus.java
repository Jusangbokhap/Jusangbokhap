package jsbh.Jusangbokhap.domain.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    COMPLETED("결제 완료"),
    FAILED("결제 취소"),
    ERROR("결제 오류");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}