package co.com.pragma.reportes.usecase.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {

    TOTAL_APPROVED_LOANS("APROBADA", "TOTAL");

    private final String reportStatus;
    private final String reportType;
}
