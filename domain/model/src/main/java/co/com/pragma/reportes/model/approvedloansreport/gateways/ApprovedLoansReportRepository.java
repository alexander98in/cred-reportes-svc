package co.com.pragma.reportes.model.approvedloansreport.gateways;

import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import reactor.core.publisher.Mono;

public interface ApprovedLoansReportRepository {

    Mono<ApprovedLoansReport> getApprovedLoansReport(String tipoReporte, String periodo);

    Mono<Void> increment(ApprovedLoansReport approvedLoansReport);
}
