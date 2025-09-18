package co.com.pragma.reportes.usecase.approvedloansreport;

import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import co.com.pragma.reportes.model.approvedloansreport.gateways.ApprovedLoansReportRepository;
import co.com.pragma.reportes.usecase.utils.ReportType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ApprovedLoansReportUseCaseImpl implements ApprovedLoansReportUseCase {

    private final ApprovedLoansReportRepository repository;
    @Override
    public Mono<ApprovedLoansReport> getTotalApprovedLoansReport() {
        return repository.getApprovedLoansReport(
                ReportType.TOTAL_APPROVED_LOANS.getReportStatus(),
                ReportType.TOTAL_APPROVED_LOANS.getReportType()
        );
    }

    @Override
    public Mono<Void> addApprovedLoan(Long inc, BigDecimal amount) {
        return repository.increment(
                ApprovedLoansReport.builder()
                        .tipoReporte("APROBADA")
                        .periodo("TOTAL")
                        .count(inc)
                        .totalAmount(amount)
                        .build()
        );
    }
}
