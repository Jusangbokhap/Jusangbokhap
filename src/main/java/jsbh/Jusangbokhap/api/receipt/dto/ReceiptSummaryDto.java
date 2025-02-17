package jsbh.Jusangbokhap.api.receipt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptSummaryDto {

    @Schema(description = "총 매출액 (취소된 금액 포함)")
    private long totalRevenue;

    @Schema(description = "취소된 결제의 총 금액")
    private long totalCanceled;

    @Schema(description = "결제 실패한 총 금액")
    private long totalFailed;

    @Schema(description = "순 매출액 (총 매출액 - 취소 금액)")
    private long netRevenue;

    @Schema(description = "매출 전표 상태별 개수")
    private ReceiptStatusCount receiptCountByStatus;


    @Getter
    @AllArgsConstructor
    public static class ReceiptStatusCount {
        @Schema(description = "정상적으로 발급된 매출 전표 개수")
        private long givenCount;

        @Schema(description = "취소된 매출 전표 개수")
        private long canceledCount;

        @Schema(description = "결제 실패로 인해 생성되지 않은 매출 전표 개수")
        private long failedCount;
    }
}
