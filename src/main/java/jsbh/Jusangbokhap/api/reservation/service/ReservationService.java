package jsbh.Jusangbokhap.api.reservation.service;

import jsbh.Jusangbokhap.api.payment.service.KakaoPayService;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentStatus;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.reservation.ReservationStatus;
import jsbh.Jusangbokhap.api.reservation.dto.ReservationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final KakaoPayService kakaoPayService;

    @Transactional
    public ReservationResponseDto cancelReservation(Long reservationId, String cancelReason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

        Optional<Payment> optionalPayment = paymentRepository.findByReservation_ReservationId(reservationId);
        optionalPayment.ifPresent(payment -> {
            if (payment.getPaymentStatus().equals(PaymentStatus.COMPLETED.name())) {
                kakaoPayService.cancelPayment(payment.getTid());
            }
        });

        reservation.cancelReservation(cancelReason);

        return convertToDto(reservation);
    }



    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getCanceledReservations(Long userId) {
        List<Reservation> canceledReservations = reservationRepository.findByGuest_UserIdAndReservationStatus(userId, ReservationStatus.CANCELED);

        return canceledReservations.stream()
                .map(reservation -> ReservationResponseDto.builder()
                        .reservationId(reservation.getReservationId())
                        .accommodationName(reservation.getAccommodation().getName())
                        .checkIn(reservation.getCheckIn())
                        .checkOut(reservation.getCheckOut())
                        .guestCount(reservation.getGuestCount())
                        .reservationStatus(reservation.getReservationStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    private ReservationResponseDto convertToDto(Reservation reservation) {
        return ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .accommodationName(reservation.getAccommodation().getName())
                .checkIn(reservation.getCheckIn())
                .checkOut(reservation.getCheckOut())
                .guestCount(reservation.getGuestCount())
                .reservationStatus(reservation.getReservationStatus().name())
                .build();
    }

}
