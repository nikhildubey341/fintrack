package com.fintrack.service;

import com.fintrack.dto.response.ReportResponseDTO;

public interface ReportService {
    ReportResponseDTO getMonthlySummary(Long userId, int month, int year);
    ReportResponseDTO getYearlySummary(Long userId, int year);
}
