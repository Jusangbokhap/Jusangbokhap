package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayApproveResponseDto {
    @Schema(description = "요청 고유 번호")
    private String aid;

    @Schema(description = "결제 고유 번호")
    private String tid;

    @Schema(description = "가맹점 코드")
    private String cid;

    @Schema(description = "정기 결제용 ID (단건 결제의 경우 null)")
    private String sid;

    @Schema(description = "주문번호")
    private String partner_order_id;

    @Schema(description = "사용자 ID")
    private String partner_user_id;

    @Schema(description = "결제 수단")
    private String payment_method_type;

    @Schema(description = "결제 금액 정보")
    private AmountDto amount;

    @Schema(description = "상품명")
    private String item_name;

    @Schema(description = "결제 생성 시간", example = "2025-02-09T14:23:06")
    private LocalDateTime created_at;

    @Schema(description = "결제 승인 시간", example = "2025-02-09T14:30:06")
    private LocalDateTime approved_at;

    @Schema(description = "카드 결제 시 사용")
    private CardInfoDto card_info;

    @Schema(description = "제 상태 상세 정보")
    private List<PaymentActionDetailDto> payment_action_details;
}
