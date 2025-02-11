package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayApproveRequestDto {
    @Schema(description = "주문번호")
    private String orderId;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "카카오페이에서 반환한 결제 승인 토큰")
    private String pgToken;

    @Schema(description = "결제 고유 번호")
    private String tid;
}
