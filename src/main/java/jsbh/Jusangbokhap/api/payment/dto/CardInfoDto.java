package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CardInfoDto {
    @Schema(description = "카드 매입사명")
    private String purchase_corp;

    @Schema(description = "카드 매입사 코드")
    private String purchase_corp_code;

    @Schema(description = "카드 발급사명")
    private String issuer_corp;

    @Schema(description = "카드 발급사 코드")
    private String issuer_corp_code;

    @Schema(description = "카카오페이 매입사명")
    private String kakaopay_purchase_corp;
    private String kakaopay_purchase_corp_code;

    @Schema(description = "카카오페이 발급사명")
    private String kakaopay_issuer_corp;
    private String kakaopay_issuer_corp_code;

    @Schema(description = "카드 BIN 번호")
    private String bin;

    @Schema(description = "카드 유형 (신용/체크)")
    private String card_type;

    @Schema(description = "할부 개월 수")
    private String install_month;

    @Schema(description = "카드사 승인번호")
    private String approved_id;

    @Schema(description = "카드 가맹점 번호")
    private String card_mid;

    @Schema(description = "무이자할부 여부")
    private String interest_free_install;

    @Schema(description = "카드 상품 코드")
    private String card_item_code;
}

