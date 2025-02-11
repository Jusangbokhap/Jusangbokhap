package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AmountDto {

    @Schema(description = "총 결제 금액")
    @Positive(message = "총 결제 금액은 0보다 커야 합니다.")
    private int total;

    @Schema(description = "비과세 금액")
    private int tax_free;

    @Schema(description = "부가세")
    private int vat;

    @Schema(description = "포인트 결제 금액")
    private int point;

    @Schema(description = "할인 금액")
    private int discount;
}
