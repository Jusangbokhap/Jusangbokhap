package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentActionDetailDto {

    @Schema(description = "요청 고유번호")
    @NotBlank(message = "요청 고유번호(AID)는 필수 입력 값입니다.")
    private String aid;

    @Schema(description = "승인 시각", example = "2025-02-09T14:30:06")
    @NotBlank(message = "승인 시각(approved_at)은 필수 입력 값입니다.")
    private String approved_at;

    @Schema(description = "결제 타입 (PAYMENT, CANCEL 등)")
    @NotBlank(message = "결제 타입은 필수 입력 값입니다.")
    private String payment_action_type;

    @Schema(description = "결제 상세 정보")
    private String payload;
}
