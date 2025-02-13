package jsbh.Jusangbokhap.api.payment.service;

import jsbh.Jusangbokhap.api.payment.dto.*;
import jsbh.Jusangbokhap.api.receipt.service.ReceiptService;
import jsbh.Jusangbokhap.api.receipt.service.KakaoPayOrderService;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.payment.PaymentStatus;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.reservation.ReservationStatus;
import jsbh.Jusangbokhap.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KakaoPayOrderService kakaoPayOrderService;
    private final ReceiptService receiptService;

    @Value("${kakao.api.admin-key}")
    private String adminKey;

    @Value("${kakao.api.approval-url}")
    private String approvalUrl;

    @Value("${kakao.api.cancel-url}")
    private String cancelUrl;

    @Value("${kakao.api.fail-url}")
    private String failUrl;

    @Value("${kakao.api.cid}")
    private String cid;

    private void saveTid(String orderId, String tid) {
        String redisKey = "payment:" + orderId;
        redisTemplate.opsForValue().set(redisKey, tid, Duration.ofMinutes(30));
    }

    private String getTid(String orderId) {
        String redisKey = "payment:" + orderId;
        String tid = redisTemplate.opsForValue().get(redisKey);

        if (tid == null) {
            Optional<Payment> existingPayment = paymentRepository.findByReservation_ReservationId(Long.valueOf(orderId));
            if (existingPayment.isPresent()) {
                return existingPayment.get().getTid();
            }
            throw new CustomException(ErrorCode.TID_NOT_FOUND, "❌ 결제 정보를 찾을 수 없습니다. 주문번호를 확인하세요.");
        }
        return tid;
    }

    /**
     * 1️⃣ 결제 준비 요청
     */
    public PayReadyResponseDto requestPayment(PayRequestDto requestDto) {
        log.info("📢 [결제 준비 요청] orderId={}, userId={}", requestDto.getOrderId(), requestDto.getUserId());

        HttpEntity<Map<String, String>> request = createPaymentRequestEntity(requestDto);

        try {
            ResponseEntity<PayReadyResponseDto> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/payment/ready", HttpMethod.POST, request, PayReadyResponseDto.class);

            if (response.getBody() == null || response.getBody().getTid() == null) {
                throw new CustomException(ErrorCode.KAKAO_PAY_INVALID_RESPONSE);
            }

            saveTid(requestDto.getOrderId(), response.getBody().getTid());
            return response.getBody();
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED, "결제 준비 요청 실패: " + e.getMessage());
        }
    }

    private HttpEntity<Map<String, String>> createPaymentRequestEntity(PayRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("partner_order_id", requestDto.getOrderId());
        requestBody.put("partner_user_id", requestDto.getUserId());
        requestBody.put("item_name", requestDto.getItemName());
        requestBody.put("quantity", String.valueOf(requestDto.getQuantity()));
        requestBody.put("total_amount", String.valueOf(requestDto.getTotalAmount()));
        requestBody.put("tax_free_amount", "0");
        requestBody.put("approval_url", approvalUrl);
        requestBody.put("cancel_url", cancelUrl);
        requestBody.put("fail_url", failUrl);

        return new HttpEntity<>(requestBody, headers);
    }

    /**
     * 2️⃣ 결제 승인 요청 (Payment 저장 로직 강화)
     */
    @Transactional
    public PayApproveResponseDto approvePayment(PayApproveRequestDto requestDto) {
        log.info("📢 [결제 승인 요청] orderId={}, userId={}", requestDto.getOrderId(), requestDto.getUserId());

        String tid = getTid(requestDto.getOrderId());
        Reservation reservation = reservationRepository.findById(Long.valueOf(requestDto.getOrderId()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

        try {
            PayApproveResponseDto response = callKakaoPayApproveApi(requestDto, tid);

            Payment payment = paymentRepository.findByTid(tid)
                    .orElseGet(() -> Payment.builder()
                            .tid(tid)
                            .reservation(reservation)
                            .user(userRepository.findById(Long.valueOf(requestDto.getUserId()))
                                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)))
                            .paymentStatus(PaymentStatus.PENDING)
                            .build());

            payment.updatePaymentOnSuccess(response);
            paymentRepository.save(payment);

            reservation.updateReservationStatus(ReservationStatus.CONFIRMED);
            receiptService.updateReceiptStatusFromKakaoPay(tid);

            return response;
        } catch (RestClientException e) {
            reservation.updateReservationStatus(ReservationStatus.PENDING);
            throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED, "결제 승인 실패: " + e.getMessage());
        }
    }

    /**
     * 3️⃣ 결제 취소 요청 (중복 취소 방지)
     */
    @Transactional
    public CancelPaymentResponseDto cancelPayment(String tid) {
        log.info("📢 [결제 취소 요청] tid={}", tid);

        Payment payment = paymentRepository.findByTid(tid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT));

        payment.validateBeforeCancel();

        ResponseEntity<CancelPaymentResponseDto> response = restTemplate.exchange(
                "https://kapi.kakao.com/v1/payment/cancel",
                HttpMethod.POST,
                createCancelRequestEntity(payment),
                CancelPaymentResponseDto.class
        );

        payment.cancel();
        paymentRepository.save(payment);

        receiptService.updateReceiptStatusFromKakaoPay(tid);

        return response.getBody();
    }

    private PayApproveResponseDto callKakaoPayApproveApi(PayApproveRequestDto requestDto, String tid) {
        String url = "https://kapi.kakao.com/v1/payment/approve";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("tid", tid);
        requestBody.put("partner_order_id", requestDto.getOrderId());
        requestBody.put("partner_user_id", requestDto.getUserId());
        requestBody.put("pg_token", requestDto.getPgToken());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<PayApproveResponseDto> response = restTemplate.exchange(
                url, HttpMethod.POST, request, PayApproveResponseDto.class);

        if (response.getBody() == null || response.getBody().getTid() == null) {
            throw new CustomException(ErrorCode.KAKAO_PAY_API_ERROR);
        }

        return response.getBody();
    }

    private HttpEntity<Map<String, String>> createCancelRequestEntity(Payment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("tid", payment.getTid());
        requestBody.put("cancel_amount", String.valueOf(payment.getPrice()));
        requestBody.put("cancel_tax_free_amount", "0");

        return new HttpEntity<>(requestBody, headers);
    }
}
