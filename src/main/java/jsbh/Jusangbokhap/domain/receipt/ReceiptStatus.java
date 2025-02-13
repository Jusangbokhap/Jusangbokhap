package jsbh.Jusangbokhap.domain.receipt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReceiptStatus {
    GIVEN("결제 영수증 발급"),
    CANCELED("취소 영수증 발급"),
    FAILED("발급 실패");

    private final String description;
}
