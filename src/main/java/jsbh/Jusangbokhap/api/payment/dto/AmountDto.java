package jsbh.Jusangbokhap.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmountDto {

    @Schema(description = "총 결제 금액")
    @JsonProperty("total_amount")
    @Positive(message = "총 결제 금액은 0보다 커야 합니다.")
    private int totalAmount;

    @Schema(description = "비과세 금액")
    @JsonProperty("tax_free_amount")
    private int taxFreeAmount;

    @Schema(description = "부가세")
    @JsonProperty("vat_amount")
    private int vatAmount;

    @Schema(description = "할인 금액")
    @JsonProperty("discount_amount")
    private int discountAmount;
}
