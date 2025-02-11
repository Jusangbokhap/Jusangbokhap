package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentActionDetailDto {

    @Schema(description = "요청 고유번호")
    private String aid;

    @Schema(description = "승인 시각")
    private String approved_at;

    @Schema(description = "결제 타입 (PAYMENT, CANCEL 등)")
    private String payment_action_type;

    @Schema(description = "결제 상세 정보")
    private String payload;
}
