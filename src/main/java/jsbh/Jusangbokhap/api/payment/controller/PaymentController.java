package jsbh.Jusangbokhap.api.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import jsbh.Jusangbokhap.api.payment.dto.PayApproveRequestDto;
import jsbh.Jusangbokhap.api.payment.dto.PayApproveResponseDto;
import jsbh.Jusangbokhap.api.payment.dto.PayReadyResponseDto;
import jsbh.Jusangbokhap.api.payment.dto.PayRequestDto;
import jsbh.Jusangbokhap.api.payment.service.KakaoPayService;
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
    public ResponseEntity<PayReadyResponseDto> requestPayment(@RequestBody PayRequestDto requestDto) {
        PayReadyResponseDto response = kakaoPayService.requestPayment(requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 승인 API", description = "사용자의 결제 승인을 처리합니다.")
    @PostMapping("/approve")
    @Transactional
    public ResponseEntity<PayApproveResponseDto> approvePayment(@RequestBody PayApproveRequestDto requestDto) {
        PayApproveResponseDto response = kakaoPayService.approvePayment(requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 취소 API", description = "결제 실패 또는 사용자가 취소했을 때 상태를 변경합니다.")
    @PostMapping("/cancel")
    @Transactional
    public ResponseEntity<Void> cancelPayment(@RequestParam String orderId) {
        kakaoPayService.cancelPayment(orderId);
        return ResponseEntity.noContent().build();
    }
}
