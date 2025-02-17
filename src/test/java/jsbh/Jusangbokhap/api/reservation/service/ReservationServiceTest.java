package jsbh.Jusangbokhap.api.reservation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import jsbh.Jusangbokhap.api.accommodation.service.AccommodationService;
import jsbh.Jusangbokhap.api.reservation.dto.ReservationRequestDto;
import jsbh.Jusangbokhap.api.reservation.dto.ReservationResponseDto;
import jsbh.Jusangbokhap.api.reservation.exception.ReservationCustomException;
import jsbh.Jusangbokhap.api.reservation.exception.ReservationErrorCode;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDates;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.reservation.ReservationRepository;
import jsbh.Jusangbokhap.domain.reservation.ReservationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private AccommodationService accommodationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("예약 성공 : 정상 요청")
    void 예약_생성_성공() {
        Long accommodationId = 1L;
        LocalDate checkIn = LocalDate.of(2025, 3, 1);
        LocalDate checkOut = LocalDate.of(2025, 3, 5);
        int guestCount = 2;


        ReservationRequestDto request = new ReservationRequestDto();
        request.setAccommodationId(accommodationId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);
        request.setGuestCount(guestCount);

        AvailableDate availableDate = new AvailableDate(checkIn, checkOut, AvailableDateStatus.AVAILABLE);
        AvailableDates availableDates = Mockito.mock(AvailableDates.class);

        when(availableDates.getDates()).thenReturn(List.of(availableDate));

        Accommodation accommodation = Accommodation.builder()
                .accommodationId(accommodationId)
                .availableDates(availableDates.getDates())
                .build();

        when(accommodationService.findById(accommodationId)).thenReturn(accommodation);

        Reservation savedReservation = Reservation.builder()
                .reservationId(100L)
                .accommodation(accommodation)
                .reservationStatus(ReservationStatus.PENDING)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponseDto response = reservationService.createReservation(request);

        assertNotNull(response);
        assertEquals(100L, response.getReservationId());
    }

    @Test
    @DisplayName("예약 실패 : 예약 가능한 날짜가 없을 경우 예외가 발생한다")
    void 예약_가능한_날짜_없음_실패() {
        Long accommodationId = 1L;
        LocalDate checkIn = LocalDate.of(2025, 3, 1);
        LocalDate checkOut = LocalDate.of(2025, 3, 5);
        int guestCount = 2;

        ReservationRequestDto request = new ReservationRequestDto();
        request.setAccommodationId(accommodationId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);
        request.setGuestCount(guestCount);

        AvailableDates availableDates = Mockito.mock(AvailableDates.class);
        when(availableDates.getDates()).thenReturn(List.of());

        Accommodation accommodation = Accommodation.builder()
                .accommodationId(accommodationId)
                .availableDates(availableDates.getDates())
                .build();

        when(accommodationService.findById(accommodationId)).thenReturn(accommodation);

        ReservationCustomException ex = assertThrows(ReservationCustomException.class, () -> {
            reservationService.createReservation(request);
        });
        assertEquals(ReservationErrorCode.NOT_FOUND_RESERVATION_DATE, ex.getErrorCode());
    }


    @Test
    @DisplayName("예약 실패 : 이미 예약된 날짜에 대해 예약을 요청하면 예외가 발생한다.")
    void 이미_예약된_숙소_실패() {
        Long accommodationId = 1L;
        LocalDate checkIn = LocalDate.of(2025, 3, 1);
        LocalDate checkOut = LocalDate.of(2025, 3, 5);
        int guestCount = 2;

        ReservationRequestDto request = new ReservationRequestDto();
        request.setAccommodationId(accommodationId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);
        request.setGuestCount(guestCount);

        AvailableDate availableDate = new AvailableDate(checkIn, checkOut, AvailableDateStatus.BOOKED);
        AvailableDates availableDates = Mockito.mock(AvailableDates.class);

        when(availableDates.getDates()).thenReturn(List.of(availableDate));
        Accommodation accommodation = Accommodation.builder()
                .accommodationId(accommodationId)
                .availableDates(availableDates.getDates())
                .build();

        when(accommodationService.findById(accommodationId)).thenReturn(accommodation);

        Assertions.assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(ReservationCustomException.class);
    }


}