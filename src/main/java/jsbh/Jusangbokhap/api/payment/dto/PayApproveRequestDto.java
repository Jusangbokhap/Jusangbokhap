package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayApproveRequestDto {

    @Schema(description = "예약번호")
    @NotBlank(message = "예약번호는 필수 입력 값입니다.")
    private String orderId;

    @Schema(description = "사용자 ID")
    @NotBlank(message = "사용자 ID는 필수 입력 값입니다.")
    private String userId;

    @Schema(description = "카카오페이에서 반환한 결제 승인 토큰")
    @NotBlank(message = "결제 승인 토큰(pgToken)은 필수 입력 값입니다.")
    private String pgToken;

    @Schema(description = "결제 고유 번호")
    @NotBlank(message = "결제 고유 번호(TID)는 필수 입력 값입니다.")
    private String tid;
}
