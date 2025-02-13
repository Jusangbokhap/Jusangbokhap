package jsbh.Jusangbokhap.api.receipt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayOrderResponseDto {
    @Schema(description = "결제 고유 번호 (TID)")
    private String tid;

    @Schema(description = "결제 상태")
    private String status;

    @Schema(description = "결제 방법")
    private String paymentMethod;

    @Schema(description = "결제 금액")
    private AmountDto amount;

    @Getter
    @Setter
    public static class AmountDto {
        @Schema(description = "총 결제 금액")
        @JsonProperty("total_amount")
        private long totalAmount;

        @Schema(description = "비과세 금액")
        @JsonProperty("tax_free_amount")
        private long taxFreeAmount;

        @Schema(description = "부가세")
        @JsonProperty("vat_amount")
        private long vatAmount;

        @Schema(description = "할인 금액")
        @JsonProperty("discount_amount")
        private long discountAmount;
    }
}
