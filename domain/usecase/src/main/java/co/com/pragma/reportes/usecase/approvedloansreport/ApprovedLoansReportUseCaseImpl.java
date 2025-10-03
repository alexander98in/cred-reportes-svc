package co.com.pragma.reportes.usecase.approvedloansreport;

import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import co.com.pragma.reportes.model.approvedloansreport.gateways.ApprovedLoansReportRepository;
import co.com.pragma.reportes.model.approvedloansreport.gateways.TaskQueue;
import co.com.pragma.reportes.usecase.utils.ReportType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ApprovedLoansReportUseCaseImpl implements ApprovedLoansReportUseCase {

    private final ApprovedLoansReportRepository repository;
    private final TaskQueue taskQueue;

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

    @Override
    public Mono<Void> sendApprovedLoanReport() {
        final String status = ReportType.TOTAL_APPROVED_LOANS.getReportStatus();
        final String type   = ReportType.TOTAL_APPROVED_LOANS.getReportType();

        return repository.getApprovedLoansReport(status, type)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.just(ApprovedLoansReport.builder()
                                .tipoReporte(status)
                                .periodo(type)
                                .count(0L)
                                .totalAmount(BigDecimal.ZERO)
                                .build())
                ))
                .flatMap(taskQueue::publishTask);
    }
}
