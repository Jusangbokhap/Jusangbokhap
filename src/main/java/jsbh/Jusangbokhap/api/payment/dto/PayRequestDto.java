package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayRequestDto {
    @Schema(description = "주문번호")
    private String orderId;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "숙소 정보")
    private String itemName;

    @Schema(description = "총 결제 금액")
    private int totalAmount;

    @Schema(description = "결제할 숙소 수량")
    private int quantity;
}
