package jsbh.Jusangbokhap.api.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jsbh.Jusangbokhap.api.reservation.dto.ReservationResponseDto;
import jsbh.Jusangbokhap.api.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "숙소 예약 취소 API", description = "사용자가 숙소 예약을 취소하면 결제까지 자동으로 취소됩니다.")
    @PostMapping("/cancel")
    public ResponseEntity<ReservationResponseDto> cancelReservation(
            @RequestParam Long reservationId,
            @RequestParam String cancelReason) {
        ReservationResponseDto response = reservationService.cancelReservation(reservationId, cancelReason);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "취소된 예약 조회 API", description = "취소된 예약 내역을 조회합니다.")
    @GetMapping("/canceled")
    public ResponseEntity<List<ReservationResponseDto>> getCanceledReservations(@RequestParam Long userId) {
        List<ReservationResponseDto> canceledReservations = reservationService.getCanceledReservations(userId);
        return ResponseEntity.ok(canceledReservations);
    }
}
