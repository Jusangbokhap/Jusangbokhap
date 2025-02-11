package jsbh.Jusangbokhap.api.payment.service;

import jsbh.Jusangbokhap.api.payment.dto.*;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.payment.PaymentStatus;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.user.repository.UserRepository;
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
import java.util.Optional;

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

        if (tid == null) {
            logger.error("❌ Redis에서 tid 조회 실패: orderId={}, Redis 데이터 없음", orderId);

            Optional<Payment> existingPayment = paymentRepository.findByReservation_ReservationId(Long.valueOf(orderId));
            if (existingPayment.isPresent()) {
                logger.warn("⚠️ 결제가 이미 DB에 존재함: orderId={}, tid={}", orderId, existingPayment.get().getTid());
                throw new CustomException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
            }

            throw new CustomException(ErrorCode.REDIS_FETCH_FAILED, "tid 조회 실패: orderId=" + orderId);
        }

        logger.info("✅ Redis에서 tid 조회 성공: orderId={}, tid={}", orderId, tid);
        return tid;
    }

    /**
     * 1️⃣ 결제 준비 요청
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
            logger.error("❌ 카카오페이 결제 요청 실패: {}", e.getMessage());
            redisTemplate.delete(requestDto.getOrderId());
            throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED);
        }
    }

    /**
     * 2️⃣ 결제 승인 요청 (PENDING → API 호출 → COMPLETED)
     */
    @Transactional
    public PayApproveResponseDto approvePayment(PayApproveRequestDto requestDto) {
        if (requestDto.getPgToken() == null || requestDto.getPgToken().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PG_TOKEN);
        }

        String tid = getTid(requestDto.getOrderId());

        Optional<Payment> existingPayment = paymentRepository.findByReservation_ReservationId(Long.valueOf(requestDto.getOrderId()));
        if (existingPayment.isPresent() && existingPayment.get().getPaymentStatus().equals(PaymentStatus.COMPLETED)) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        Reservation reservation = reservationRepository.findById(Long.valueOf(requestDto.getOrderId()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

        Payment payment = Payment.builder()
                .tid(tid)
                .reservation(reservation)
                .user(userRepository.findById(Long.valueOf(requestDto.getUserId()))
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)))
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        try {
            PayApproveResponseDto responseDto = callKakaoPayApproveApi(requestDto, tid);

            reservation.confirmReservation();

            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPrice(responseDto.getAmount().getTotal());
            payment.setPaymentMethod(responseDto.getPayment_method_type());

            paymentRepository.save(payment);
            reservationRepository.save(reservation);

            return responseDto;
        } catch (RestClientException e) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED);
        }
    }

    /**
     * 2️⃣ 결제 취소
     */
    @Transactional
    public CancelPaymentResponseDto cancelPayment(String tid) {
        Payment payment = paymentRepository.findByTid(tid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT));

        if (payment.getPaymentStatus().equals(PaymentStatus.CANCELED)) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }

        String url = "https://kapi.kakao.com/v1/payment/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("tid", tid);
        requestBody.put("cancel_amount", String.valueOf(payment.getPrice()));
        requestBody.put("cancel_tax_free_amount", "0");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<CancelPaymentResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, request, CancelPaymentResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new CustomException(ErrorCode.KAKAO_PAY_API_ERROR);
        }

        payment.setPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);

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
            throw new CustomException(ErrorCode.KAKAO_PAY_INVALID_RESPONSE);
        }
        return response.getBody();
    }
}
