package jsbh.Jusangbokhap.api.payment.service;

import jsbh.Jusangbokhap.api.payment.dto.*;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.payment.PaymentStatus;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.user.User;
import jsbh.Jusangbokhap.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoPayService.class);
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RedisTemplate<String, String> redisTemplate;

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
        redisTemplate.opsForValue().set(orderId, tid, Duration.ofMinutes(30));
    }

    private String getTid(String orderId) {
        String tid = redisTemplate.opsForValue().get(orderId);
        if (tid != null && paymentRepository.existsByTid(tid)) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }
        return tid;
    }

    /**
     * 1️⃣ 결제 준비 요청 (카카오페이 단건 결제)
     */
    public PayReadyResponseDto requestPayment(PayRequestDto requestDto) {
        String url = "https://kapi.kakao.com/v1/payment/ready";
        logger.info("📢 [결제 요청] 숙소ID={}, 사용자ID={}, 결제금액={}",
                requestDto.getOrderId(), requestDto.getUserId(), requestDto.getTotalAmount());

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

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<PayReadyResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, PayReadyResponseDto.class);

            if (response.getBody() == null || response.getBody().getTid() == null) {
                throw new CustomException(ErrorCode.KAKAO_PAY_INVALID_RESPONSE);
            }

            saveTid(requestDto.getOrderId(), response.getBody().getTid());
            return response.getBody();

        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED, e.getMessage());
        }
    }

    /**
     * 2️⃣ 결제 승인 요청
     */
    @Transactional
    public PayApproveResponseDto approvePayment(PayApproveRequestDto requestDto) {
        String url = "https://kapi.kakao.com/v1/payment/approve";

        String tid = getTid(requestDto.getOrderId());
        if (tid == null) {
            throw new CustomException(ErrorCode.REDIS_FETCH_FAILED);
        }

        logger.info("📢 [결제 승인 요청] orderId={}, userId={}, pgToken={}, TID={}",
                requestDto.getOrderId(), requestDto.getUserId(), requestDto.getPgToken(), tid);

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

        try {
            ResponseEntity<PayApproveResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, PayApproveResponseDto.class);

            if (response.getBody() == null || response.getBody().getTid() == null) {
                throw new CustomException(ErrorCode.KAKAO_PAY_INVALID_RESPONSE);
            }

            savePayment(requestDto, response.getBody());

            return response.getBody();

        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED, e.getMessage());
        }
    }

    private void savePayment(PayApproveRequestDto requestDto, PayApproveResponseDto response) {
        Long userId = Long.valueOf(requestDto.getUserId());
        Long reservationId = Long.valueOf(requestDto.getOrderId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT));

        Payment payment = Payment.builder()
                .user(user)
                .reservation(reservation)
                .price(response.getAmount().getTotal())
                .paymentMethod(response.getPayment_method_type())
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        paymentRepository.save(payment);
        logger.info("✅ [결제 정보 저장 완료] 결제 ID: {}", payment.getPaymentId());
    }

    /**
     * 3️⃣ 결제 취소 API (fail_url, cancel_url 후처리)
     */
    @Transactional
    public void cancelPayment(String orderId) {
        Payment payment = paymentRepository.findByReservation_ReservationId(Long.valueOf(orderId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT));

        payment.setPaymentStatus(PaymentStatus.FAILED.name());
        paymentRepository.save(payment);

        logger.info("❌ [결제 취소 완료] 주문번호: {}", orderId);
    }

}
