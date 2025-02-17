package jsbh.Jusangbokhap.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequestDto {

    @Schema(description = "숙소 번호")
    private Long accommodationId;

    @Schema(description = "체크인 날짜", example = "2024-07-15")
    private LocalDate checkIn;

    @Schema(description = "체크아웃 날짜", example = "2024-07-20")
    private LocalDate checkOut;

    @Schema(description = "예약한 총 투숙객 수")
    private Integer guestCount;

}
