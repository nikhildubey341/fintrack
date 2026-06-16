package com.fintrack.controller;

import com.fintrack.dto.response.ApiResponse;
import com.fintrack.dto.response.ReportResponseDTO;
import com.fintrack.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "APIs for financial reports and analytics")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/user/{userId}/monthly-summary")
    @Operation(summary = "Get monthly income vs expense summary")
    public ResponseEntity<ApiResponse<ReportResponseDTO>> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {
        int m = month == 0 ? LocalDate.now().getMonthValue() : month;
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.success("Monthly summary fetched",
                reportService.getMonthlySummary(userId, m, y)));
    }

    @GetMapping("/user/{userId}/yearly-summary")
    @Operation(summary = "Get yearly income vs expense summary")
    public ResponseEntity<ApiResponse<ReportResponseDTO>> getYearlySummary(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.success("Yearly summary fetched",
                reportService.getYearlySummary(userId, y)));
    }
}
