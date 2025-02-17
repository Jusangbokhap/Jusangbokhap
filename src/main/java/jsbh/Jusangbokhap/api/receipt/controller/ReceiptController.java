package jsbh.Jusangbokhap.api.receipt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jsbh.Jusangbokhap.api.receipt.service.ReceiptService;
import jsbh.Jusangbokhap.api.receipt.dto.ReceiptSummaryDto;
import jsbh.Jusangbokhap.domain.receipt.ReceiptStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @Operation(
            summary = "숙소 매출 집계 조회",
            description = "특정 숙소의 전체 매출전표(영수증) 데이터를 조회합니다. (기간 설정 가능)\n" +
                    "기간을 설정하지 않으면 해당 숙소의 현재 달에 대한 매출을 조회합니다.",
            parameters = {
                    @Parameter(name = "accommodationId", description = "숙소 ID", required = true),
                    @Parameter(name = "startDate", description = "조회 시작일", example = "2024-01-01T00:00:00"),
                    @Parameter(name = "endDate", description = "조회 종료일", example = "2024-02-01T23:59:59")
            }
    )
    @GetMapping("/summary")
    public ResponseEntity<ReceiptSummaryDto> getTotalRevenue(
            @RequestParam Long accommodationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        ReceiptSummaryDto revenueSummary = receiptService.getTotalRevenueByAccommodation(accommodationId, startDate, endDate);
        return ResponseEntity.ok(revenueSummary);
    }

    @GetMapping("/detailed-summary")
    public ResponseEntity<ReceiptSummaryDto> getDetailedRevenue(
            @RequestParam Long accommodationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        ReceiptSummaryDto revenueSummary = receiptService.getDetailedRevenueSummary(accommodationId, startDate, endDate);
        return ResponseEntity.ok(revenueSummary);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ReceiptStatus>> getReceiptStatus(@RequestParam Long accommodationId) {
        List<ReceiptStatus> receiptStatuses = receiptService.getReceiptStatusByAccommodation(accommodationId);
        return ResponseEntity.ok(receiptStatuses);
    }
}
