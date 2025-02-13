package jsbh.Jusangbokhap.api.facility.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import jsbh.Jusangbokhap.api.accommodation.service.AccommodationHostService;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationAddress;
import jsbh.Jusangbokhap.domain.facility.FacilityCategory;
import jsbh.Jusangbokhap.domain.facility.repository.FacilityRepository;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class FacilityServiceTest {

    private static MockWebServer mockWebServer;
    private WebClient webClient;
    private FacilityService facilityService;
    private FacilityRepository facilityRepository;
    private AccommodationHostService hostService;

    @BeforeAll
    static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        HttpUrl baseUrl = mockWebServer.url("/");
        webClient = WebClient.builder().baseUrl(baseUrl.toString()).build();
        facilityRepository = mock(FacilityRepository.class);
        hostService = mock(AccommodationHostService.class);
        facilityService = new FacilityService(webClient, facilityRepository, hostService);
    }

    @Test
    void 시설_업데이트_개수_조회_성공_테스트() {
        Long accommodationId = 1L;

        Accommodation dummyAccommodation = Accommodation.builder()
                .address(AccommodationAddress.builder()
                        .longitude(100.0)
                        .latitude(200.0)
                        .build())
                .build();

        when(hostService.getAccommodationByAccommodationId(accommodationId))
                .thenReturn(dummyAccommodation);
        when(facilityRepository.findByAccommodationId(accommodationId))
                .thenReturn(new ArrayList<>());

        int numCategories = FacilityCategory.values().length;
        String jsonResponse = "{\n"
                + "    \"documents\": [\n"
                + "        { \"address_name\": \"서울 강남구 대치동 942-4\", \"category_group_code\": \"CS2\", \"category_group_name\": \"편의점\", \"category_name\": \"가정,생활 > 편의점 > GS25\", \"distance\": \"127\", \"id\": \"1119231969\", \"phone\": \"02-567-1564\", \"place_name\": \"GS25 강남쥬비스점\", \"place_url\": \"http://place.map.kakao.com/1119231969\", \"road_address_name\": \"서울 강남구 삼성로 434\", \"x\": \"127.057612205935\", \"y\": \"37.5056683821046\" }\n"
                + "    ],\n"
                + "    \"meta\": {\n"
                + "        \"is_end\": true,\n"
                + "        \"pageable_count\": 6,\n"
                + "        \"same_name\": null,\n"
                + "        \"total_count\": 6\n"
                + "    }\n"
                + "}";
        for (int i = 0; i < numCategories; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setBody(jsonResponse)
                    .addHeader("Content-Type", "application/json"));
        }

        Map<String, Integer> result = facilityService.getUpdatedFacilityCounts(accommodationId);

        for (FacilityCategory category : FacilityCategory.values()) {
            assertEquals(6, result.get(category.getDescription()),
                    "카테고리 " + category.getDescription() + "의 시설 개수가 6이어야 함");
        }
    }
}
