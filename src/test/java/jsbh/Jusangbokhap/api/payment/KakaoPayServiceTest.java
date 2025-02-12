package jsbh.Jusangbokhap.api.payment;

import jsbh.Jusangbokhap.api.payment.dto.*;
import jsbh.Jusangbokhap.api.payment.service.KakaoPayService;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.payment.PaymentStatus;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.user.User;
import jsbh.Jusangbokhap.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KakaoPayServiceTest {

    @Mock private RestTemplate restTemplate;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;
    @Mock private PaymentRepository paymentRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReservationRepository reservationRepository;

    @InjectMocks private KakaoPayService kakaoPayService;

    @BeforeEach
    void setUp() {

    }

    /**
     * 📌 1️⃣ 결제 요청 테스트 (성공)
     */
    @Test
    void requestPayment_Success() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), any());

        PayRequestDto requestDto = PayRequestDto.builder()
                .orderId("12345")
                .userId("user1")
                .itemName("숙소 A")
                .totalAmount(50000)
                .quantity(1)
                .build();

        PayReadyResponseDto fakeResponse = PayReadyResponseDto.builder()
                .tid("TID123456")
                .next_redirect_pc_url("https://payment.kakao.com")
                .build();

        ResponseEntity<PayReadyResponseDto> responseEntity = new ResponseEntity<>(fakeResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PayReadyResponseDto.class)))
                .thenReturn(responseEntity);

        PayReadyResponseDto response = kakaoPayService.requestPayment(requestDto);

        assertNotNull(response);
        assertEquals("TID123456", response.getTid());
        assertEquals("https://payment.kakao.com", response.getNext_redirect_pc_url());
    }

    /**
     * 📌 2️⃣ 결제 승인 테스트 (성공)
     */
    @Test
    void approvePayment_Success() {
        PayApproveRequestDto requestDto = PayApproveRequestDto.builder()
                .orderId("12345")
                .userId("9999")
                .pgToken("valid_pg_token")
                .tid("TID123456")
                .build();

        PayApproveResponseDto fakeResponse = PayApproveResponseDto.builder()
                .tid("TID123456")
                .payment_method_type("CARD")
                .amount(AmountDto.builder().total(50000).build())
                .approved_at(LocalDateTime.now())
                .build();

        User mockUser = mock(User.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        Reservation mockReservation = mock(Reservation.class);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(mockReservation));

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(requestDto.getOrderId())).thenReturn("TID123456");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PayApproveResponseDto.class)))
                .thenReturn(new ResponseEntity<>(fakeResponse, HttpStatus.OK));

        PayApproveResponseDto response = kakaoPayService.approvePayment(requestDto);

        assertNotNull(response);
        assertEquals("TID123456", response.getTid());
        assertEquals("CARD", response.getPayment_method_type());
        assertEquals(50000, response.getAmount().getTotal());
    }

    /**
     * 📌 3️⃣ 결제 승인 실패 테스트 (잘못된 PG 토큰)
     */
    @Test
    void approvePayment_Fail_InvalidPgToken() {
        PayApproveRequestDto requestDto = PayApproveRequestDto.builder()
                .orderId("12345")
                .userId("user1")
                .pgToken("")
                .tid("TID123456")
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> {
            kakaoPayService.approvePayment(requestDto);
        });

        assertEquals(ErrorCode.INVALID_PG_TOKEN, exception.getErrorCode());
    }

    /**
     * 📌 4️⃣ 결제 취소 테스트 (성공)
     */
    @Test
    void cancelPayment_Success() {
        String tid = "TID123456";

        Reservation mockReservation = mock(Reservation.class);

        Payment payment = Payment.builder()
                .tid(tid)
                .reservation(mockReservation)
                .price(50000)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        when(paymentRepository.findByTid(tid)).thenReturn(Optional.of(payment));

        CancelPaymentResponseDto fakeResponse = CancelPaymentResponseDto.builder()
                .tid(tid)
                .paymentStatus("CANCELED")
                .cancelAmount("50000")
                .build();

        ResponseEntity<CancelPaymentResponseDto> responseEntity = new ResponseEntity<>(fakeResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(CancelPaymentResponseDto.class)))
                .thenReturn(responseEntity);

        CancelPaymentResponseDto response = kakaoPayService.cancelPayment(tid);

        assertNotNull(response);
        assertEquals("CANCELED", response.getPaymentStatus());
        assertEquals("50000", response.getCancelAmount());
    }

    /**
     * 📌 5️⃣ 결제 취소 실패 테스트 (TID가 존재하지 않을 경우)
     */
    @Test
    void cancelPayment_Fail_TidNotFound() {
        String tid = "INVALID_TID";
        when(paymentRepository.findByTid(tid)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            kakaoPayService.cancelPayment(tid);
        });

        assertEquals(ErrorCode.NOT_FOUND_PAYMENT, exception.getErrorCode());
    }
}
