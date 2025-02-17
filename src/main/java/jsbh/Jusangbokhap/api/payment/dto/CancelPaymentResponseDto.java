package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CancelPaymentResponseDto {

    @Schema(description = "예약번호")
    @NotBlank(message = "예약번호는 필수 입력 값입니다.")
    private String orderId;

    @Schema(description = "결제 고유 번호 (TID)")
    @NotBlank(message = "결제 고유 번호(TID)는 필수 입력 값입니다.")
    private String tid;

    @Schema(description = "결제 상태 (취소 여부)")
    @NotBlank(message = "결제 상태는 필수 입력 값입니다.")
    private String paymentStatus;

    @Schema(description = "결제 취소 응답 메시지")
    @NotBlank(message = "결제 취소 응답 메시지는 필수 입력 값입니다.")
    private String message;

    @Schema(description = "취소된 결제 금액")
    @Positive(message = "취소된 결제 금액은 0보다 커야 합니다.")
    private String cancelAmount;

    @Schema(description = "결제 승인 시간", example = "2024-02-10T15:30:45")
    private String approvedAt;

    @Schema(description = "결제 취소 시간", example = "2024-02-11T10:15:30")
    private String canceledAt;
}
