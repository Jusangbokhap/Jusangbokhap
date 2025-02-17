package jsbh.Jusangbokhap.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReservationResponseDto {

    @Schema(description = "예약 ID")
    private Long reservationId;

    @Schema(description = "숙소 이름")
    private String accommodationName;

    @Schema(description = "체크인 날짜", example = "2024-07-15")
    private LocalDate checkIn;

    @Schema(description = "체크아웃 날짜", example = "2024-07-20")
    private LocalDate checkOut;

    @Schema(description = "예약한 총 투숙객 수")
    private Integer guestCount;

    @Schema(description = "예약 상태", example = "CANCELED (취소됨), CONFIRMED (확정됨), PENDING (대기 중)")
    private String reservationStatus;
}
