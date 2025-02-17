package jsbh.Jusangbokhap.api.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jsbh.Jusangbokhap.api.payment.dto.*;
import jsbh.Jusangbokhap.api.payment.service.KakaoPayService;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final KakaoPayService kakaoPayService;

    @Operation(summary = "결제 요청 API", description = "사용자의 결제 요청을 처리합니다.")
    @PostMapping("/request")
    public ResponseEntity<PayReadyResponseDto> requestPayment(@Valid @RequestBody PayRequestDto requestDto) {
        PayReadyResponseDto response = kakaoPayService.requestPayment(requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 승인 API", description = "사용자의 결제 승인을 처리합니다.")
    @PostMapping("/approve")
    @Transactional
    public ResponseEntity<PayApproveResponseDto> approvePayment(@Valid @RequestBody PayApproveRequestDto requestDto) {
        PayApproveResponseDto response = kakaoPayService.approvePayment(requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 취소 API", description = "tid(결제 고유번호)로 결제를 취소합니다.")
    @PostMapping("/cancel")
    @Transactional
    public ResponseEntity<CancelPaymentResponseDto> cancelPayment(
            @RequestParam(value = "tid", required = false) String tid) {
        if (tid == null || tid.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "tid 값은 필수입니다.");
        }
        CancelPaymentResponseDto response = kakaoPayService.cancelPayment(tid);
        return ResponseEntity.ok(response);
    }
}
