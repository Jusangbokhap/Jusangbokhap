package jsbh.Jusangbokhap.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayReadyResponseDto {

    @Schema(description = "결제 고유 번호")
    @NotBlank(message = "결제 고유 번호(TID)는 필수 입력 값입니다.")
    private String tid;

    @Schema(description = "결제 페이지 URL (PC)")
    @NotBlank(message = "결제 페이지 URL(PC)은 필수 입력 값입니다.")
    private String next_redirect_pc_url;

    @Schema(description = "결제 페이지 URL (모바일)")
    @NotBlank(message = "결제 페이지 URL(모바일)은 필수 입력 값입니다.")
    private String next_redirect_mobile_url;

    @Schema(description = "결제 페이지 URL (앱)")
    private String next_redirect_app_url;

    @Schema(description = "Android 앱 스킴")
    private String android_app_scheme;

    @Schema(description = "iOS 앱 스킴")
    private String ios_app_scheme;

    @Schema(description = "결제 준비가 완료된 시각", example = "2024-02-11T14:30:00")
    @NotBlank(message = "결제 준비 완료 시각은 필수 입력 값입니다.")
    private String created_at;
}
