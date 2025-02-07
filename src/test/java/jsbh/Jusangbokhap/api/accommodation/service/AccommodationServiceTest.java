package jsbh.Jusangbokhap.api.accommodation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
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
    @DisplayName("숙소 생성 성공")
    void 숙소_생성_성공() {
        AccommodationRequest.Register request = new AccommodationRequest.Register(
                "Korea",
                "Description",
                1000,
                "HOTEL",
                2,
                1
        );

        when(accommodationRepository.save(any(Accommodation.class)))
                .thenAnswer(invocation -> {
                    Accommodation accommodation = invocation.getArgument(0);
                    ReflectionTestUtils.setField(accommodation, "accommodationId", 1L);
                    return accommodation;
                });

        AccommodationResponse.Register response = (AccommodationResponse.Register) accommodationService.createAccommodation(
                request);
        assertNotNull(response);
        assertEquals(1L, response.accommodationId());
        assertEquals(1L, response.userId());
    }

    @Test
    @DisplayName("숙소 생성 실패: 최대 인원수가 1보다 작으면 예외 발생")
    void 숙소_생성_실패_최대인원_1미만_예외_발생() {
        AccommodationRequest.Register request = new AccommodationRequest.Register(
                "Korea",
                "Description",
                1000,
                "HOTEL",
                0,
                1
        );

        assertThrows(RuntimeException.class, () -> accommodationService.createAccommodation(request));
    }

    @Test
    @DisplayName("숙소 생성 실패: 최대 인원수가 100보다 크면 예외 발생")
    void 숙소_생성_실패_최대인원_100초과_예외_발생() {
        AccommodationRequest.Register request = new AccommodationRequest.Register(
                "Korea",
                "Description",
                1000,
                "HOTEL",
                101,
                1
        );

        assertThrows(RuntimeException.class, () -> accommodationService.createAccommodation(request));
    }

    @Test
    @DisplayName("숙소 생성 실패: 잘못된 숙소 타입이면 예외 발생")
    void 숙소_생성_실패_잘못된_숙소_타입_예외_발생() {
        AccommodationRequest.Register request = new AccommodationRequest.Register(
                "Korea",
                "Description",
                1000,
                "NULL",
                2,
                1
        );

        assertThrows(IllegalArgumentException.class, () -> accommodationService.createAccommodation(request));
    }
}
