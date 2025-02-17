package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayRequestDto {

    @Schema(description = "예약번호")
    @NotBlank(message = "예약번호는 필수 입력 값입니다.")
    private String orderId;

    @Schema(description = "사용자 ID")
    @NotBlank(message = "사용자 ID는 필수 입력 값입니다.")
    private String userId;

    @Schema(description = "숙소 정보")
    @NotBlank(message = "숙소 정보는 필수 입력 값입니다.")
    private String itemName;

    @Schema(description = "총 결제 금액")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    private int totalAmount;

    @Schema(description = "결제할 숙소 수량")
    @Positive(message = "숙소 수량은 0보다 커야 합니다.")
    private int quantity;
}
