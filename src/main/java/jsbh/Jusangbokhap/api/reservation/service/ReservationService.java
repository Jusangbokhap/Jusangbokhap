package jsbh.Jusangbokhap.api.reservation.service;

import static jsbh.Jusangbokhap.api.reservation.exception.ReservationErrorCode.NOT_FOUND_RESERVATION;

import jsbh.Jusangbokhap.api.accommodation.service.AccommodationService;
import jsbh.Jusangbokhap.api.payment.service.KakaoPayService;
import jsbh.Jusangbokhap.api.reservation.dto.ReservationRequestDto;
import jsbh.Jusangbokhap.api.reservation.exception.ReservationCustomException;
import jsbh.Jusangbokhap.api.reservation.exception.ReservationErrorCode;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDates;
import jsbh.Jusangbokhap.domain.payment.PaymentRepository;
import jsbh.Jusangbokhap.domain.payment.Payment;
import jsbh.Jusangbokhap.domain.payment.PaymentStatus;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.reservation.ReservationStatus;
import jsbh.Jusangbokhap.api.reservation.dto.ReservationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final AccommodationService accommodationService;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final KakaoPayService kakaoPayService;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto request) {

        Accommodation foundAccommodation = accommodationService.findById(request.getAccommodationId());
        AvailableDates availableDates = foundAccommodation.getAvailableDates();

        AvailableDate availableDate = availableDates.findReservableDate(request.getCheckIn(), request.getCheckOut());

        if (availableDate == null) {
            throw new ReservationCustomException(ReservationErrorCode.NOT_FOUND_RESERVATION_DATE);
        }

        if (!availableDate.isAvailable()) {
            throw new ReservationCustomException(ReservationErrorCode.ALREADY_BOOKED_DATE);
        }

        try {
            Reservation reservation = attemptReservation(foundAccommodation, availableDate, request.getGuestCount());
            return convertToDto(reservation);
        } catch (StaleObjectStateException e) {
            throw new ReservationCustomException(ReservationErrorCode.ALREADY_BOOKED_DATE);
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Reservation attemptReservation(Accommodation accommodation, AvailableDate availableDate, int guests) {

        Reservation reservation = Reservation.builder()
                .accommodation(accommodation)
                .checkIn(availableDate.getStartDate())
                .checkOut(availableDate.getEndDate())
                .guestCount(guests)
                .reservationStatus(ReservationStatus.PENDING)
                .build();

        availableDate.updateStatus();

        reservation = reservationRepository.saveAndFlush(reservation);

        return reservation;
    }


    @Transactional
    public ReservationResponseDto cancelReservation(Long reservationId, String cancelReason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION));

        Payment payment = paymentRepository.findByReservation_ReservationId(reservationId).orElse(null);
        if (payment != null && payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            kakaoPayService.cancelPayment(payment.getTid());
        }

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

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getReservations(Long userId) {
        List<Reservation> canceledReservations = reservationRepository.findByGuest_UserId(userId);

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

    @Transactional(readOnly = true)
    public ReservationResponseDto getReservation(Long userId, Long reservationId) {
        Reservation res = reservationRepository.findByGuest_UserIdAndReservationId(userId, reservationId)
                .orElseThrow(() -> new ReservationCustomException(NOT_FOUND_RESERVATION));

        return convertToDto(res);
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
