package jsbh.Jusangbokhap.api.accommodation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateRequest;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationType;
import org.springframework.format.annotation.DateTimeFormat;

public interface AccommodationRequest {

    record Create(

            @Schema(description = "숙소명", example = "London Calling in Hesedorf #No.2")
            String title,

            @Schema(description = "상호명", example = "Henann Garden Resort")
            String businessName,

            @Schema(description = "시도", example = "서울특별시")
            String sido,

            @Schema(description = "시군수", example = "강남구")
            String sigungu,

            @Schema(description = "읍면동", example = "역삼동")
            String eupmyeondong,

            @Schema(description = "상세 주소", example = "144-17번지 201호")
            String detail,

            @Schema(description = "경도", example = "18.29345")
            Double longitude,

            @Schema(description = "위도", example = "9.15093")
            Double latitude,

            @Schema(description = "숙소 설명")
            String description,

            @Schema(description = "1박 가격", example = "100000")
            Integer price,

            @Schema(description = "숙소 형태", example = "HOTEL")
            String accommodationType,

            @Schema(description = "최대 인원", example = "10")
            Integer guests,

            @Schema(description = "호스트 ID", example = "1")
            Integer userId,

            @Schema(description = "예약 가능 날짜",
                    example = "["
                            + "{\"startDate\": \"2025-02-01\","
                            + " \"endDate\": \"2025-02-10\","
                            + " \"status\": \"AVAILABLE\"}"
                            + "]")
            List<AvailableDateRequest> availableDates

    ) implements AccommodationRequest {
    }

    record Update(
            @Schema(description = "숙소명", example = "London Calling in Hesedorf #No.2")
            String title,

            @Schema(description = "시도", example = "서울특별시")
            String sido,

            @Schema(description = "시군수", example = "강남구")
            String sigungu,

            @Schema(description = "읍면동", example = "역삼동")
            String eupmyeondong,

            @Schema(description = "상세 주소", example = "144-17번지 201호")
            String detail,

            @Schema(description = "경도", example = "18.29345")
            Double longitude,

            @Schema(description = "위도", example = "9.15093")
            Double latitude,

            @Schema(description = "숙소 설명")
            String description,

            @Schema(description = "1박 가격", example = "100000")
            Integer price,

            @Schema(description = "숙소 형태", example = "HOTEL")
            String accommodationType,

            @Schema(description = "최대 인원", example = "10")
            Integer guests
    ) implements AccommodationRequest {
    }

    record Search(
            @Schema(description = "검색할 상호명", example = "Henann Garden Resort")
            String businessName,

            @Schema(description = "시도", example = "서울특별시")
            String sido,

            @Schema(description = "시군구", example = "강남구")
            String sigungu,

            @Schema(description = "읍면동", example = "역삼동")
            String eupmyeondong,

            @Schema(description = "상세 주소", example = "144-17번지 201호")
            String detail,

            @Schema(description = "여행 시작 날짜 (yyyy-MM-dd 형식)", example = "2025-02-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate checkin,

            @Schema(description = "여행 종료 날짜 (yyyy-MM-dd 형식)", example = "2025-02-10")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate checkout,

            @Schema(description = "최소 가격", example = "1")
            Integer minPrice,

            @Schema(description = "최대 가격", example = "1")
            Integer maxPrice,

            @Schema(description = "게스트 수", example = "2")
            Integer guests,

            @Schema(description = "숙소 형태", example = "HOTEL")
            AccommodationType type

    ) implements AccommodationRequest {
        public Search {
            checkin  = checkin  != null ? checkin  : LocalDate.now();
            checkout = checkout != null ? checkout : checkin.plusDays(1);
        }
    }
}
