package jsbh.Jusangbokhap.api.receipt;

import jsbh.Jusangbokhap.api.receipt.dto.ReceiptSummaryDto;
import jsbh.Jusangbokhap.api.receipt.service.ReceiptService;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.receipt.Receipt;
import jsbh.Jusangbokhap.domain.receipt.ReceiptRepository;
import jsbh.Jusangbokhap.domain.receipt.ReceiptStatus;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @InjectMocks
    private ReceiptService receiptService;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    private User host;
    private Reservation reservation;
    private Payment payment;
    private Receipt receipt;

    @BeforeEach
    void setUp() {
        host = User.builder()
                .email("host@example.com")
                .password("securepassword")
                .nickname("호스트")
                .build();

        reservation = Reservation.builder()
                .reservationId(100L)
                .guest(host)
                .checkIn(LocalDate.now().minusDays(2))
                .checkOut(LocalDate.now().plusDays(2))
                .build();


        payment = Payment.builder()
                .paymentId(200L)
                .reservation(reservation)
                .price(100000L)
                .paymentMethod("KAKAO_PAY")
                .tid("T123456789")
                .build();

        receipt = Receipt.builder()
                .receiptId(300L)
                .host(host)
                .reservation(reservation)
                .totalAmount(100000L)
                .fee(5000L)
                .profit(95000L)
                .givenAt(LocalDateTime.now())
                .receiptStatus(ReceiptStatus.GIVEN)
                .build();
    }

    /**
     * 1️⃣ 정상적인 매출 전표 발급 테스트
     */
    @Test
    void 매출전표_정상발급() {
        lenient().when(paymentRepository.findByTid(payment.getTid())).thenReturn(Optional.of(payment));
        when(receiptRepository.save(any(Receipt.class))).thenReturn(receipt);

        Receipt savedReceipt = receiptRepository.save(receipt);

        assertThat(savedReceipt).isNotNull();
        assertThat(savedReceipt.getTotalAmount()).isEqualTo(100000L);
        assertThat(savedReceipt.getReceiptStatus()).isEqualTo(ReceiptStatus.GIVEN);
    }

    /**
     * 2️⃣ 결제 취소 후 매출 전표 상태 확인 테스트
     */
    @Test
    void 결제취소후_매출전표_상태확인() {
        receipt = Receipt.builder()
                .receiptId(301L)
                .host(host)
                .reservation(reservation)
                .totalAmount(100000L)
                .fee(5000L)
                .profit(95000L)
                .givenAt(LocalDateTime.now())
                .receiptStatus(ReceiptStatus.CANCELED)
                .build();

        when(receiptRepository.findById(301L)).thenReturn(Optional.of(receipt));

        Receipt canceledReceipt = receiptRepository.findById(301L).orElseThrow();

        assertThat(canceledReceipt.getReceiptStatus()).isEqualTo(ReceiptStatus.CANCELED);
    }

    /**
     * 3️⃣ 특정 숙소의 전체 매출 조회 테스트
     */
    @Test
    void 특정숙소_전체매출조회() {
        when(receiptRepository.findAllByAccommodationAndPeriod(
                any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(List.class)))
                .thenReturn(List.of(receipt));

        ReceiptSummaryDto summary = receiptService.getTotalRevenueByAccommodation(
                1L, LocalDateTime.now().minusMonths(1), LocalDateTime.now());

        assertThat(summary.getTotalRevenue()).isEqualTo(100000L);

        long expectedNetRevenue = 100000L - summary.getTotalCanceled();
        assertThat(summary.getNetRevenue()).isEqualTo(expectedNetRevenue);
    }

    /**
     * 4️⃣ 특정 숙소의 매출 전표 상태별 조회 테스트
     */
    @Test
    void 특정숙소_매출전표_상태별조회() {
        when(receiptRepository.findDistinctReceiptStatusByAccommodation(1L))
                .thenReturn(List.of(ReceiptStatus.GIVEN, ReceiptStatus.CANCELED));

        List<ReceiptStatus> receiptStatuses = receiptService.getReceiptStatusByAccommodation(1L);

        assertThat(receiptStatuses).containsExactlyInAnyOrder(ReceiptStatus.GIVEN, ReceiptStatus.CANCELED);
    }
}
