package jsbh.Jusangbokhap.domain.receipt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReceiptStatus {
    GIVEN("지급"),
    CANCELED("취소");

    private final String description;

}