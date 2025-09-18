package co.com.pragma.reportes.api.dto.response;

public record LoansReportResponseDTO (
        String reportType,
        String period,
        Long totalApprovedLoans,
        String totalAmountApprovedLoans
) {
}
