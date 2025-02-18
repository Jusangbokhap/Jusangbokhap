// package jsbh.Jusangbokhap.api.accommodation.service;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;

// import java.time.LocalDate;
// import java.util.List;
// import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
// import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
// import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationCustomException;
// import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateRequest;
// import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateCustomException;
// import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
// import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
// import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.util.ReflectionTestUtils;

// @ExtendWith(MockitoExtension.class)
// class AccommodationServiceTest {

//     @Mock
//     private AccommodationRepository accommodationRepository;

//     @InjectMocks
//     private AccommodationService accommodationService;

//     @Test
//     @DisplayName("숙소 생성 성공: 유효한 요청일 경우 숙소 등록 된다")
//     void createAccommodation_success_whenRequestValid() {
//         Create request = createAccommodationRequest("Test Title", "서울특별시", 100000, "HOTEL", 10, 1);

//         when(accommodationRepository.save(any(Accommodation.class)))
//                 .thenAnswer(invocation -> {
//                     Accommodation accommodation = invocation.getArgument(0);
//                     // Accommodation 엔티티의 ID 필드를 Reflection을 통해 설정
//                     ReflectionTestUtils.setField(accommodation, "accommodationId", 1L);
//                     return accommodation;
//                 });

//         AccommodationResponse.Create response = (AccommodationResponse.Create) accommodationService.create(request);
//         assertNotNull(response);
//         assertEquals(1L, response.accommodationId());
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 최대 인원수가 1명 미만이면 예외 발생")
//     void createAccommodation_fail_whenGuestsLessThanOne() {
//         Create request = createAccommodationRequest("Test Title", "서울특별시", 100000, "HOTEL", 0, 1);
//         assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 최대 인원수가 100명 초과이면 예외 발생")
//     void createAccommodation_fail_whenGuestsExceedHundred() {
//         Create request = createAccommodationRequest("Test Title", "서울특별시", 100000, "HOTEL", 101, 1);
//         assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 1박당 가격이 최소 가격 미만이면 예외 발생")
//     void createAccommodation_fail_whenPriceBelowMinimum() {
//         Create request = createAccommodationRequest("Test Title", "서울특별시", 0, "HOTEL", 10, 1);
//         assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 1박당 가격이 최대 가격 초과이면 예외 발생")
//     void createAccommodation_fail_whenPriceExceedsMaximum() {
//         Create request = createAccommodationRequest("Test Title", "서울특별시", 10000001, "HOTEL", 10, 1);
//         assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 잘못된 숙소 타입이면 예외 발생")
//     void createAccommodation_fail_whenInvalidAccommodationType() {
//         Create request = createAccommodationRequest("Test Title", "서울특별시", 100000, "INVALID_TYPE", 10, 1);
//         assertThrows(AccommodationCustomException.class, () -> accommodationService.create(request));
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 예약 가능 날짜가 겹치면 예외 발생")
//     void createAccommodation_fail_whenAvailableDatesOverlap() {
//         // AvailableDateRequest에서 시작일과 종료일이 같은 경우(겹침 처리)를 가정
//         LocalDate startDate = LocalDate.of(2025, 2, 1);
//         LocalDate endDate = LocalDate.of(2025, 2, 1);
//         List<AvailableDateRequest> availableDates = List.of(
//                 new AvailableDateRequest(startDate, endDate, AvailableDateStatus.AVAILABLE)
//         );

//         Create request = createAccommodationRequest("Test Title", "서울특별시", 100000, "HOTEL", 10, 1, availableDates);
//         assertThrows(AvailableDateCustomException.class, () -> accommodationService.create(request));
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 예약 시작 날짜가 종료 날짜보다 늦으면 예외 발생")
//     void createAccommodation_fail_whenStartDateAfterEndDate() {
//         LocalDate startDate = LocalDate.of(2025, 2, 10);
//         LocalDate endDate = LocalDate.of(2025, 2, 1);
//         List<AvailableDateRequest> availableDates = List.of(
//                 new AvailableDateRequest(startDate, endDate, AvailableDateStatus.AVAILABLE)
//         );

//         Create request = createAccommodationRequest("Test Title", "서울특별시", 100000, "HOTEL", 10, 1, availableDates);
//         assertThrows(AvailableDateCustomException.class, () -> accommodationService.create(request));
//     }

//     @Test
//     @DisplayName("숙소 생성 실패: 예약 날짜 정보가 누락되면 예외 발생")
//     void 숙소_생성_실패_예약날짜_누락_예외발생() {
//         AvailableDateRequest availableDateRequest = new AvailableDateRequest(null, null, null);
//         List<AvailableDateRequest> availableDateRequests = List.of(availableDateRequest);
//         Create request = new Create("경기도", "경기도 호텔", 100, "HOTEL", 1, 1, availableDateRequests);

//         assertThrows(AvailableDateCustomException.class, () -> accommodationService.create(request));
//     }



//     private Create createAccommodationRequest(String address, String des, int price, String type, int people,
//                                               int userId) {
//         LocalDate startDate = LocalDate.of(2025, 2, 1);
//         LocalDate endDate = LocalDate.of(2025, 2, 7);
//         return new Create(
//                 address,
//                 des,
//                 price,
//                 type,
//                 people,
//                 userId,
//                 List.of(new AvailableDateRequest(startDate, endDate, AvailableDateStatus.AVAILABLE))
//     void createAccommodation_fail_whenAvailableDateInfoMissing() {
//         List<AvailableDateRequest> availableDates = List.of(
//                 new AvailableDateRequest(null, null, null)
//         );
//         Create request = createAccommodationRequest("Test Title", "서울특별시", 100000, "HOTEL", 10, 1, availableDates);
//         assertThrows(AvailableDateCustomException.class, () -> accommodationService.create(request));
//     }

//     // 기본 예약 가능 날짜를 포함한 Create DTO 생성 헬퍼 메서드
//     private Create createAccommodationRequest(String title, String sido, int price, String type, int guests, int userId) {
//         LocalDate startDate = LocalDate.of(2025, 2, 1);
//         LocalDate endDate = LocalDate.of(2025, 2, 7);
//         List<AvailableDateRequest> availableDates = List.of(
//                 new AvailableDateRequest(startDate, endDate, AvailableDateStatus.AVAILABLE)
//         );
//         return new Create(
//                 title,                           // 숙소명
//                 "Test Business",                 // 상호명
//                 sido,                          // 시도
//                 "Test Sigungu",                // 시군수
//                 "Test Eupmyeondong",           // 읍면동
//                 "Test Detail Address",         // 상세 주소
//                 127.0,                         // 경도
//                 37.0,                          // 위도
//                 "Test Accommodation Description", // 숙소 설명
//                 price,                         // 1박 가격
//                 type,                          // 숙소 타입
//                 guests,                        // 최대 인원
//                 userId,                        // 호스트 ID
//                 availableDates                 // 예약 가능 날짜
//         );
//     }

//     // 예약 가능 날짜를 외부에서 지정할 수 있도록 한 오버로딩 헬퍼 메서드
//     private Create createAccommodationRequest(String title, String sido, int price, String type, int guests, int userId, List<AvailableDateRequest> availableDates) {
//         return new Create(
//                 title,
//                 "Test Business",
//                 sido,
//                 "Test Sigungu",
//                 "Test Eupmyeondong",
//                 "Test Detail Address",
//                 127.0,
//                 37.0,
//                 "Test Accommodation Description",
//                 price,
//                 type,
//                 guests,
//                 userId,
//                 availableDates
//         );
//     }
// }
