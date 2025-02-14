package jsbh.Jusangbokhap.api.accommodation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.service.AccommodationHostService;
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
public class AccommodationHostController {

    private final AccommodationHostService accommodationHostService;

    @Operation(
            summary = "숙소 등록 API",
            description ="숙소를 등록하고, 등록된 숙소 번호를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "숙소 등록 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 등록 실패")
            }
    )
    @PostMapping
    public ResponseEntity<AccommodationResponse> createAccommodation(@RequestBody Create request) {
        return new ResponseEntity<>(accommodationHostService.create(request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "숙소 조회(숙소 ID) API",
            description ="숙소 번호로 숙소를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "숙소 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 조회 실패")
            }
    )
    @GetMapping("/{accommodationId}")
    public ResponseEntity<AccommodationResponse> getAccommodationById(@PathVariable Long accommodationId) {
        return ResponseEntity.ok(accommodationHostService.findByAccommodationId(accommodationId));
    }


    @Operation(
            summary = "숙소 조회(호스트 ID) API",
            description ="호스트 번호로 호스트가 등록한 숙소를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "숙소 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "숙소 조회 실패")
            }
    )
    @GetMapping("/users/{hostId}")
    public ResponseEntity<List<AccommodationResponse>> getAccommodationsByHostId(@PathVariable Long hostId) {
        return ResponseEntity.ok(accommodationHostService.findByHostId(hostId));
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
        return ResponseEntity.ok(accommodationHostService.update(accommodationId, request));
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
        return ResponseEntity.ok(accommodationHostService.delete(accommodationId));
    }

}
