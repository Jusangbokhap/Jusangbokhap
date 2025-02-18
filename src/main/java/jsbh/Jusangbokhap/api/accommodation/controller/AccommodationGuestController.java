package jsbh.Jusangbokhap.api.accommodation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.io.IOException;
import java.util.List;

import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationCoordSearchRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationCoordSearchResponse;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.service.AccommodationGuestService;
import jsbh.Jusangbokhap.api.search.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/accommodations/search")
@RequiredArgsConstructor
public class AccommodationGuestController {
    private final AccommodationGuestService accommodationGuestService;
    private final SearchKeywordService keywordService;

    @Operation(
            summary = "숙소 검색 API",
            description = "게스트는 여러 필터링 조건을 선택하여 원하는 숙소를 조회할 수 있다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "숙소 검색 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 검색 실패")
            }
    )
    @GetMapping
    public List<AccommodationResponse> search(@ParameterObject @ModelAttribute AccommodationRequest.Search search) throws IOException {
        keywordService.saveKeyword(search);
        return accommodationGuestService.find(search);
    }

    @GetMapping("/coordinate")
    public List<AccommodationCoordSearchResponse> search(AccommodationCoordSearchRequest coordSearchRequest) {
        return accommodationGuestService.findByCoordinate(coordSearchRequest);
    }

}
