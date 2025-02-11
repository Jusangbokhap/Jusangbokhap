package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CardInfoDto {
    @Schema(description = "카드 매입사명")
    @NotBlank(message = "카드 매입사명은 필수 입력 값입니다.")
    private String purchase_corp;

    @Schema(description = "카드 매입사 코드")
    private String purchase_corp_code;

    @Schema(description = "카드 발급사명")
    @NotBlank(message = "카드 발급사명은 필수 입력 값입니다.")
    private String issuer_corp;

    @Schema(description = "카드 발급사 코드")
    private String issuer_corp_code;

    @Schema(description = "카드 BIN 번호")
    @NotBlank(message = "카드 BIN 번호는 필수 입력 값입니다.")
    private String bin;

    @Schema(description = "카드 유형 (신용/체크)")
    @NotBlank(message = "카드 유형은 필수 입력 값입니다.")
    private String card_type;

    @Schema(description = "할부 개월 수")
    private String install_month;

    @Schema(description = "카드사 승인번호")
    @NotBlank(message = "카드사 승인번호는 필수 입력 값입니다.")
    private String approved_id;

    @Schema(description = "카드 가맹점 번호")
    private String card_mid;

    @Schema(description = "무이자할부 여부", example = "Y")
    private String interest_free_install;

    @Schema(description = "카드 상품 코드")
    private String card_item_code;
}
