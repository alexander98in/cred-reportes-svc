package co.com.pragma.reportes.model.approvedloansreport.gateways;

import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import reactor.core.publisher.Mono;

public interface TaskQueue {
    Mono<Void> publishTask(ApprovedLoansReport report);
}
