package jsbh.Jusangbokhap.api.accommodation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationCustomException;
import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateRequest;
import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateCustomException;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private AccommodationService accommodationService;

    @Test
    @DisplayName("숙소 생성 성공: 유효한 요청일 경우 숙소 등록 된다")
    void 숙소_생성_성공_유효한_요청() {
        Create request = createAccommodationRequest("경기도", "경기도 호텔", 100000, "HOTEL", 1, 1);

        when(accommodationRepository.save(any(Accommodation.class)))
                .thenAnswer(invocation -> {
                    Accommodation accommodation = invocation.getArgument(0);
                    ReflectionTestUtils.setField(accommodation, "accommodationId", 1L);
                    return accommodation;
                });

        AccommodationResponse.Create response = (AccommodationResponse.Create) accommodationService.create(request);
        assertNotNull(response);
        assertEquals(1L, response.accommodationId());
    }

    @Test
    @DisplayName("숙소 생성 실패: 최대 인원수가 1명 미만이면 예외 발생")
    void 숙소_생성_실패_최대인원_1명미만_예외발생() {
        Create request = createAccommodationRequest("경기도", "경기도 호텔", 100000, "HOTEL", 0, 1);
        assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
    }

    @Test
    @DisplayName("숙소 생성 실패: 최대 인원수가 100명 초과이면 예외 발생")
    void 숙소_생성_실패_최대인원_100명초과_예외발생() {
        Create request = createAccommodationRequest("경기도", "경기도 호텔", 100000, "HOTEL", 101, 1);
        assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
    }

    @Test
    @DisplayName("숙소 생성 실패: 1박당 가격이 최소 가격 미만이면 예외 발생")
    void 숙소_생성_실패_최소가격_미만_예외발생() {
        Create request = createAccommodationRequest("경기도", "경기도 호텔", 0, "HOTEL", 100, 1);
        assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
    }


    @Test
    @DisplayName("숙소 생성 실패: 1박당 가격이 최대 가격 초과이면 예외 발생")
    void 숙소_생성_실패_최대가격_초과_예외발생() {
        Create request = createAccommodationRequest("경기도", "경기도 호텔", 10000001, "HOTEL", 100, 1);
        assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
    }


    @Test
    @DisplayName("숙소 생성 실패: 잘못된 숙소 타입이면 예외 발생")
    void 숙소_생성_실패_잘못된_숙소타입_예외발생() {
        Create request = createAccommodationRequest("경기도", "경기도 호텔", 100, "NULL", 1, 1);
        assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
    }

    @Test
    @DisplayName("숙소 생성 실패: 예약 가능 날짜가 겹치면 예외 발생")
    void 숙소_생성_실패_예약날짜_겹침_예외발생() {
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 1);

        List<AvailableDateRequest> availableDateRequests = List.of(
                new AvailableDateRequest(startDate, endDate, AvailableDateStatus.AVAILABLE));

        Create request = new Create("경기도", "경기도 호텔", 100, "HOTEL", 1, 1, availableDateRequests);

        assertThrows(AvailableDateCustomException.class, () -> accommodationService.create(request));
    }


    @Test
    @DisplayName("숙소 생성 실패: 예약 시작 날짜가 종료 날짜보다 늦으면 예외 발생")
    void 숙소_생성_실패_예약시작날짜가_종료날짜보다늦으면_예외발생() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 2, 1);

        List<AvailableDateRequest> availableDateRequests = List.of(
                new AvailableDateRequest(startDate, endDate, AvailableDateStatus.AVAILABLE));

        Create request = new Create("경기도", "경기도 호텔", 100, "HOTEL", 1, 1, availableDateRequests);

        assertThrows(AvailableDateCustomException.class, () -> accommodationService.create(request));
    }

    @Test
    @DisplayName("숙소 생성 실패: 예약 날짜 정보가 누락되면 예외 발생")
    void 숙소_생성_실패_예약날짜_누락_예외발생() {
        AvailableDateRequest availableDateRequest = new AvailableDateRequest(null, null, null);
        List<AvailableDateRequest> availableDateRequests = List.of(availableDateRequest);
        Create request = new Create("경기도", "경기도 호텔", 100, "HOTEL", 1, 1, availableDateRequests);

        assertThrows(AvailableDateCustomException.class, () -> accommodationService.create(request));
    }



    private Create createAccommodationRequest(String address, String des, int price, String type, int people,
                                              int userId) {
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 7);
        return new Create(
                address,
                des,
                price,
                type,
                people,
                userId,
                List.of(new AvailableDateRequest(startDate, endDate, AvailableDateStatus.AVAILABLE))
        );
    }
}
