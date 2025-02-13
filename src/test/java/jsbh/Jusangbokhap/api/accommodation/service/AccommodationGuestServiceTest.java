package jsbh.Jusangbokhap.api.accommodation.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.types.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse.Read;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationAddress;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationCapacity;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationPrice;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationType;
import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccommodationGuestServiceTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private AccommodationGuestService accommodationGuestService;

    List<Accommodation> accommodations = new ArrayList<>();

    @BeforeEach
    void init() {
        accommodations.add(Accommodation.builder()
                .businessName("Henann Garden Resort")
                .accommodationPrice(AccommodationPrice.from(1000))
                .availableDates(List.of(new AvailableDate(
                        LocalDate.parse("2025-02-02"),
                        LocalDate.parse("2025-02-10"),
                        AvailableDateStatus.AVAILABLE)))
                        .maxGuests(AccommodationCapacity.from(10))
                .accommodationType(AccommodationType.CAMPING)

                .address(new AccommodationAddress(
                        1L,
                        "서울특별시",
                        "강남구",
                        "역삼동",
                        "144-17 201호",
                        18.1234,
                        19.1323
                ))
                .build());

        accommodations.add(Accommodation.builder()
                .businessName("Asya Premier Suites")
                .accommodationPrice(AccommodationPrice.from(20000))
                .availableDates(List.of(new AvailableDate(
                        LocalDate.parse("2025-02-03"),
                        LocalDate.parse("2025-02-10"),
                        AvailableDateStatus.AVAILABLE)))
                        .accommodationType(AccommodationType.HOTEL)
                .maxGuests(AccommodationCapacity.from(2))
                .address(new AccommodationAddress(
                        1L,
                        "경기도 수원시",
                        "영통구",
                        "인계동",
                        "100-1 101호",
                        8.1234,
                        9.1323
                ))
                .build());
    }


    @Test
    @DisplayName("숙소 조회 성공 : 올바른 상호명")
    void 상호명으로_숙소_검색_성공() {
        String businessName = "Henann Garden Resort";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                businessName,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(com.querydsl.core.types.Predicate.class)))
                .thenAnswer(invocation -> accommodations.stream()
                        .filter(acc -> acc.getBusinessName().equals(businessName))
                        .toList());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        assertEquals(1, responses.size(), "검색 결과 숙소 개수가 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(com.querydsl.core.types.Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 실패 : 존재하지 않는 상호명으로 숙소 검색")
    void 상호명으로_숙소_검색_실패() {
        String businessName = "Nonexistent Business";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                businessName,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(com.querydsl.core.types.Predicate.class)))
                .thenReturn(List.of());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        assertEquals(0, responses.size(), "검색 결과 숙소 개수는 0이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(com.querydsl.core.types.Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 성공 : 서울특별시와 같은 시도 정보로 숙소 검색")
    void 시도주소로_숙소_검색_성공() {
        String sido = "서울특별시";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                sido,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class)))
                .thenAnswer(invocation -> accommodations.stream()
                        .filter(acc -> acc.getAddress().getSido().equals(sido))
                        .toList());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        assertEquals(1, responses.size(), "검색 결과 숙소 개수가 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 실패 : 존재하지 않는 시도 주소로 숙소 검색")
    void 시도주소로_숙소_검색_실패() {
        String sido = "아무대나";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                sido,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class)))
                .thenAnswer(invocation -> accommodations.stream()
                        .filter(acc -> acc.getAddress().getSido().equals(sido))
                        .toList());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        assertEquals(0, responses.size(), "검색 결과 숙소 개수가 0이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 성공 : 시군구 정보로 숙소 검색")
    void 시군구주소로_숙소_검색_성공() {
        String sigungu = "강남구";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                sigungu,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class)))
                .thenAnswer(invocation -> accommodations.stream()
                        .filter(acc -> acc.getAddress().getSigungu().equals(sigungu))
                        .toList());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        assertEquals(1, responses.size(), "검색 결과 숙소 개수가 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 실패 : 존재하지 않은 시군구로 숙소 검색")
    void 시군구주소로_숙소_검색_실패() {
        String sigungu = "시군구";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                sigungu,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class)))
                .thenAnswer(invocation -> accommodations.stream()
                        .filter(acc -> acc.getAddress().getSigungu().equals(sigungu))
                        .toList());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        assertEquals(0, responses.size(), "검색 결과 숙소 개수가 0이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 성공 : 읍면동 정보로 숙소 검색")
    void 읍면동주소로_숙소_검색_성공() {
        String eupmyeondong = "역삼동";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                null,
                eupmyeondong,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class)))
                .thenAnswer(invocation -> accommodations.stream()
                        .filter(acc -> acc.getAddress().getEupmyeondong().equals(eupmyeondong))
                        .toList());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        assertEquals(1, responses.size(), "검색 결과 숙소 개수가 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 실패 : 존재하지 않은 읍면동 정보로 숙소 검색")
    void 읍면동주소로_숙소_검색_실패() {
        String eupmyeondong = "없동";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                null,
                eupmyeondong,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class)))
                .thenAnswer(invocation -> accommodations.stream()
                        .filter(acc -> acc.getAddress().getEupmyeondong().equals(eupmyeondong))
                        .toList());

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);

        for (AccommodationResponse respons : responses) {
            Read respons1 = (Read) respons;
        }

        assertEquals(0, responses.size(), "검색 결과 숙소 개수가 0이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 성공 : 체크인/체크아웃 날짜로 숙소 검색")
    void 체크인체크아웃날짜로_숙소_검색_성공() {
        String filterCheckin = "2025-02-02";
        String filterCheckout = "2025-02-10";
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                null,
                filterCheckin,
                filterCheckout,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class))).thenAnswer(
                invocation -> accommodations.stream()
                        .filter(acc -> {
                            AvailableDate availableDate = acc.getAvailableDates().getDates().get(0);
                            return !availableDate.getCheckin().isAfter(LocalDate.parse(filterCheckin))
                                    && !availableDate.getCheckout().isBefore(LocalDate.parse(filterCheckout));
                        })
                        .toList()
        );

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);
        assertEquals(1, responses.size(), "검색 결과 숙소 개수는 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 성공 : 가격 범위로 숙소 검색")
    void 가격범위로_숙소_검색_성공() {
        int minPrice = 500;
        int maxPrice = 1500;
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                minPrice,
                maxPrice,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class))).thenAnswer(
                invocation -> accommodations.stream()
                        .filter(acc -> {
                            int price = acc.getAccommodationPrice().getPrice();
                            return price >= minPrice && price <= maxPrice;
                        })
                        .toList()
        );

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);
        assertEquals(1, responses.size(), "검색 결과 숙소 개수는 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 성공 : 게스트 수로 숙소 검색")
    void 게스트수로_숙소_검색_성공() {
        int requiredGuests = 8;
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                requiredGuests,
                null,
                null,
                null
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class))).thenAnswer(
                invocation -> accommodations.stream()
                        .filter(acc -> acc.getMaxGuests().getMaxGuest() >= requiredGuests)
                        .toList()
        );

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);
        assertEquals(1, responses.size(), "검색 결과 숙소 개수는 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }

    @Test
    @DisplayName("숙소 조회 성공 : 숙소 형태로 숙소 검색")
    void 숙소형태로_검색_성공() {
        AccommodationType type = AccommodationType.HOTEL;
        AccommodationRequest.Search filter = new AccommodationRequest.Search(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                type
        );

        when(accommodationRepository.findAvailableAccommodations(any(Predicate.class))).thenAnswer(
                invocation -> accommodations.stream()
                        .filter(acc -> acc.getAccommodationType() == type)
                        .toList()
        );

        List<AccommodationResponse> responses = accommodationGuestService.find(filter);
        assertEquals(1, responses.size(), "검색 결과 숙소 개수는 1이어야 한다");
        verify(accommodationRepository, times(1))
                .findAvailableAccommodations(any(Predicate.class));
    }
}