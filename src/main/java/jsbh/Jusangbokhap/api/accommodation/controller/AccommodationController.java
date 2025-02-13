package jsbh.Jusangbokhap.api.accommodation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @Operation(
            summary = "숙소 등록 API",
            description ="숙소를 등록하고, 등록된 숙소 번호를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "숙소 등록 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 등록 실패")
            }
    )
    @PostMapping
    public ResponseEntity<AccommodationResponse> createAccommodation(
            @RequestBody Create request) {
        return new ResponseEntity<>(accommodationService.create(request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "숙소 조회 API",
            description ="숙소 번호로 숙소를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "숙소 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 조회 실패")
            }
    )
    @GetMapping("/{accommodationId}")
    public ResponseEntity<AccommodationResponse> getAccommodation(@PathVariable Long accommodationId) {
        return ResponseEntity.ok(accommodationService.find(accommodationId));
    }

    @Operation(
            summary = "숙소 수정 API",
            description ="숙소 번호로 숙소를 찾아 수정하고, 수정된 숙소 번호를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "숙소 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 수정 실패")
            }
    )
    @PutMapping("/{accommodationId}")
    public ResponseEntity<AccommodationResponse> updateAccommodation(@PathVariable Long accommodationId,
                                                                     @RequestBody AccommodationRequest.Update request) {
        return ResponseEntity.ok(accommodationService.update(accommodationId, request));
    }


    @Operation(
            summary = "숙소 삭제 API",
            description ="숙소 번호로 숙소를 찾아 삭제하고, 삭제된 숙소 번호를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "숙소 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 삭제 실패")
            }
    )
    @DeleteMapping("/{accommodationId}")
    public ResponseEntity<AccommodationResponse> deleteAccommodation(@PathVariable Long accommodationId) {
        return ResponseEntity.ok(accommodationService.delete(accommodationId));
    }
}
