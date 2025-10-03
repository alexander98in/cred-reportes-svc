package co.com.pragma.reportes.usecase.approvedloansreport;

import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ApprovedLoansReportUseCase {

    Mono<ApprovedLoansReport> getTotalApprovedLoansReport();

    Mono<Void> addApprovedLoan(Long inc, BigDecimal amount);

    Mono<Void> sendApprovedLoanReport();
}
