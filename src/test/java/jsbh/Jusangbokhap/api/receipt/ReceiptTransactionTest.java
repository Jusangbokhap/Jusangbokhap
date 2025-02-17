package jsbh.Jusangbokhap.api.receipt;

import jsbh.Jusangbokhap.api.payment.dto.*;
import jsbh.Jusangbokhap.api.payment.service.KakaoPayService;
import jsbh.Jusangbokhap.api.receipt.dto.KakaoPayOrderResponseDto;
import jsbh.Jusangbokhap.api.receipt.service.KakaoPayOrderService;
import jsbh.Jusangbokhap.api.receipt.service.ReceiptService;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.payment.PaymentStatus;
import jsbh.Jusangbokhap.domain.receipt.Receipt;
import jsbh.Jusangbokhap.domain.receipt.ReceiptRepository;
import jsbh.Jusangbokhap.domain.receipt.ReceiptStatus;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.reservation.ReservationStatus;
import jsbh.Jusangbokhap.domain.user.User;
import jsbh.Jusangbokhap.domain.user.repository.UserRepository;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptTransactionTest {

    @Mock
    private KakaoPayOrderService kakaoPayOrderService;
    @Mock
    private ReceiptRepository receiptRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private KakaoPayService kakaoPayService;

    @InjectMocks
    private ReceiptService receiptService;

    /**
     * 1️⃣ 주문 조회 API 기반으로 자동 업데이트 (성공)
     */
    @Test
    void updateReceiptStatusFromKakaoPay_Success() {
        String tid = "TID123456";

        Accommodation mockAccommodation = mock(Accommodation.class);
        Reservation mockReservation = mock(Reservation.class);
        when(mockReservation.getAccommodation()).thenReturn(mockAccommodation);

        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getReservation()).thenReturn(mockReservation);
        when(paymentRepository.findByTid(tid)).thenReturn(Optional.of(mockPayment));

        KakaoPayOrderResponseDto.AmountDto fakeAmount = new KakaoPayOrderResponseDto.AmountDto();
        fakeAmount.setTotalAmount(50000L);
        fakeAmount.setTaxFreeAmount(0L);
        fakeAmount.setVatAmount(5000L);
        fakeAmount.setDiscountAmount(0L);

        KakaoPayOrderResponseDto fakeResponse = new KakaoPayOrderResponseDto();
        fakeResponse.setTid(tid);
        fakeResponse.setStatus("SUCCESS");
        fakeResponse.setAmount(fakeAmount);

        when(kakaoPayOrderService.getOrderStatus(tid)).thenReturn(fakeResponse);

        assertDoesNotThrow(() -> receiptService.updateReceiptStatusFromKakaoPay(tid));
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    /**
     * 2️⃣ 주문 조회 API 기반으로 자동 업데이트 (결제 취소 반영)
     */
    @Test
    void updateReceiptStatusFromKakaoPay_Canceled() {
        String tid = "TID123456";

        Accommodation mockAccommodation = mock(Accommodation.class);
        Reservation mockReservation = mock(Reservation.class);
        when(mockReservation.getAccommodation()).thenReturn(mockAccommodation);

        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getReservation()).thenReturn(mockReservation);
        when(paymentRepository.findByTid(tid)).thenReturn(Optional.of(mockPayment));

        KakaoPayOrderResponseDto.AmountDto fakeAmount = new KakaoPayOrderResponseDto.AmountDto();
        fakeAmount.setTotalAmount(50000L);
        fakeAmount.setTaxFreeAmount(0L);
        fakeAmount.setVatAmount(5000L);
        fakeAmount.setDiscountAmount(0L);

        KakaoPayOrderResponseDto fakeResponse = new KakaoPayOrderResponseDto();
        fakeResponse.setTid(tid);
        fakeResponse.setStatus("CANCELED");
        fakeResponse.setAmount(fakeAmount);

        when(kakaoPayOrderService.getOrderStatus(tid)).thenReturn(fakeResponse);

        assertDoesNotThrow(() -> receiptService.updateReceiptStatusFromKakaoPay(tid));
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    /**
     * 3️⃣ 매출전표(Receipt)에 취소된 금액(cancelAmount)이 정상적으로 반영
     */
    @Test
    void adjustRevenueAfterCancellation_ShouldUpdateTotalCanceled() {
        Long accommodationId = 1L;
        long cancelAmount = 20000L;

        Receipt mockReceipt1 = Receipt.builder().totalAmount(50000L).cancelAmount(10000L).receiptStatus(ReceiptStatus.GIVEN).build();
        Receipt mockReceipt2 = Receipt.builder().totalAmount(30000L).cancelAmount(5000L).receiptStatus(ReceiptStatus.CANCELED).build();

        when(receiptRepository.findAllByAccommodationAndPeriod(eq(accommodationId), any(), any(), any()))
                .thenReturn(List.of(mockReceipt1, mockReceipt2));

        assertDoesNotThrow(() -> receiptService.adjustRevenueAfterCancellation(accommodationId, cancelAmount));

        verify(receiptRepository, times(1)).findAllByAccommodationAndPeriod(eq(accommodationId), any(), any(), any());
    }
}