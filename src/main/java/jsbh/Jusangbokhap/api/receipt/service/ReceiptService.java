package jsbh.Jusangbokhap.api.receipt.service;

import jsbh.Jusangbokhap.api.receipt.dto.KakaoPayOrderResponseDto;
import jsbh.Jusangbokhap.api.receipt.dto.ReceiptSummaryDto;
import jsbh.Jusangbokhap.domain.receipt.Receipt;
import jsbh.Jusangbokhap.domain.receipt.ReceiptRepository;
import jsbh.Jusangbokhap.domain.receipt.ReceiptStatus;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final KakaoPayOrderService kakaoPayOrderService;
    private final ReceiptRepository receiptRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void updateReceiptStatusFromKakaoPay(String tid) {
        KakaoPayOrderResponseDto responseDto = kakaoPayOrderService.getOrderStatus(tid);

        Payment payment = paymentRepository.findByTid(tid)
                .orElseThrow(() -> new RuntimeException("❌ 결제 내역을 찾을 수 없습니다. TID: " + tid));

        if (responseDto.getAmount() == null) {
            throw new RuntimeException("❌ 카카오페이 API 응답에서 결제 금액 정보가 누락되었습니다. TID: " + tid);
        }

        KakaoPayOrderResponseDto.AmountDto amountDto = responseDto.getAmount();

        long totalAmount = amountDto.getTotalAmount();
        long taxFree = amountDto.getTaxFreeAmount();
        long vat = amountDto.getVatAmount();
        long discount = amountDto.getDiscountAmount();

        long fee = vat + discount;
        long profit = totalAmount - fee;

        ReceiptStatus receiptStatus;
        if ("SUCCESS".equals(responseDto.getStatus())) {
            payment.updatePaymentOnSuccessFromOrder(responseDto);
            receiptStatus = ReceiptStatus.GIVEN;
        } else if ("CANCELED".equals(responseDto.getStatus())) {
            payment.updatePaymentOnFailure();
            receiptStatus = ReceiptStatus.CANCELED;
        } else {
            log.warn("❌ 결제 실패로 인해 영수증을 발급하지 않습니다. tid={}", responseDto.getTid());
            payment.updatePaymentOnFailure();
            return;
        }

        Receipt receipt = createReceipt(payment, receiptStatus, fee, profit);
        receiptRepository.save(receipt);
    }

    private Receipt createReceipt(Payment payment, ReceiptStatus status, long fee, long profit) {
        Reservation reservation = payment.getReservation();
        return Receipt.builder()
                .host(reservation.getAccommodation().getHost())
                .reservation(reservation)
                .accommodation(reservation.getAccommodation())
                .totalAmount(payment.getPrice().longValue())
                .fee(fee)
                .profit(profit)
                .givenAt(LocalDateTime.now())
                .receiptStatus(status)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReceiptStatus> getReceiptStatusByAccommodation(Long accommodationId) {
        return receiptRepository.findDistinctReceiptStatusByAccommodation(accommodationId);
    }

    @Transactional(readOnly = true)
    public ReceiptSummaryDto getTotalRevenueByAccommodation(Long accommodationId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Receipt> receipts = receiptRepository.findAllByAccommodationAndPeriod(
                accommodationId, startDate, endDate, List.of(ReceiptStatus.GIVEN, ReceiptStatus.CANCELED, ReceiptStatus.FAILED)
        );

        long totalGiven = receipts.stream()
                .filter(r -> r.getReceiptStatus() == ReceiptStatus.GIVEN)
                .mapToLong(Receipt::getTotalAmount)
                .sum();

        long totalCanceled = receipts.stream()
                .filter(r -> r.getReceiptStatus() == ReceiptStatus.CANCELED)
                .mapToLong(Receipt::getTotalAmount)
                .sum();

        long totalFailed = receipts.stream()
                .filter(r -> r.getReceiptStatus() == ReceiptStatus.FAILED)
                .mapToLong(Receipt::getTotalAmount)
                .sum();

        long netRevenue = totalGiven - totalCanceled;

        long givenCount = receipts.stream().filter(r -> r.getReceiptStatus() == ReceiptStatus.GIVEN).count();
        long canceledCount = receipts.stream().filter(r -> r.getReceiptStatus() == ReceiptStatus.CANCELED).count();
        long failedCount = receipts.stream().filter(r -> r.getReceiptStatus() == ReceiptStatus.FAILED).count();

        return new ReceiptSummaryDto(
                totalGiven,
                totalCanceled,
                totalFailed,
                netRevenue,
                new ReceiptSummaryDto.ReceiptStatusCount(givenCount, canceledCount, failedCount)
        );
    }

    @Transactional(readOnly = true)
    public ReceiptSummaryDto getDetailedRevenueSummary(Long accommodationId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Receipt> receipts = receiptRepository.findAllByAccommodationAndPeriod(
                accommodationId, startDate, endDate, List.of(ReceiptStatus.GIVEN, ReceiptStatus.CANCELED, ReceiptStatus.FAILED)
        );

        long totalGiven = receipts.stream()
                .filter(r -> r.getReceiptStatus() == ReceiptStatus.GIVEN)
                .mapToLong(Receipt::getTotalAmount)
                .sum();

        long totalCanceled = receipts.stream()
                .filter(r -> r.getReceiptStatus() == ReceiptStatus.CANCELED)
                .mapToLong(Receipt::getTotalAmount)
                .sum();

        long totalFailed = receipts.stream()
                .filter(r -> r.getReceiptStatus() == ReceiptStatus.FAILED)
                .mapToLong(Receipt::getTotalAmount)
                .sum();

        long netRevenue = totalGiven - totalCanceled;

        long givenCount = receipts.stream().filter(r -> r.getReceiptStatus() == ReceiptStatus.GIVEN).count();
        long canceledCount = receipts.stream().filter(r -> r.getReceiptStatus() == ReceiptStatus.CANCELED).count();
        long failedCount = receipts.stream().filter(r -> r.getReceiptStatus() == ReceiptStatus.FAILED).count();

        return new ReceiptSummaryDto(
                totalGiven,
                totalCanceled,
                totalFailed,
                netRevenue,
                new ReceiptSummaryDto.ReceiptStatusCount(givenCount, canceledCount, failedCount)
        );
    }
}
