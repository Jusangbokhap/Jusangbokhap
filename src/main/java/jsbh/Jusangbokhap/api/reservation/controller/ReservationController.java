package jsbh.Jusangbokhap.api.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jsbh.Jusangbokhap.api.reservation.dto.ReservationRequestDto;
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

    @Operation(summary = "숙소 예약 API", description = "사용자가 선택한 숙소에 대해 예약을 생성한다.")
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @Operation(summary = "숙소 예약 조회 API", description = "사용자의 예약 내역을 모두 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getReservations(Long userId) {
        return ResponseEntity.ok(reservationService.getReservations(userId));
    }

    @Operation(
            summary = "특정 예약 상세 조회 API",
            description = "사용자 ID와 예약 ID를 기반으로 특정 예약의 상세 정보를 조회합니다.")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDto> getReservation(@PathVariable Long reservationId, @RequestParam Long userId) {
        return ResponseEntity.ok(reservationService.getReservation(userId, reservationId));
    }

    @Operation(summary = "숙소 예약 취소 API", description = "사용자가 예약 취소를 요청하면 예약한 숙소 취소 및 결제한 금액이 환불됩니다.")
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