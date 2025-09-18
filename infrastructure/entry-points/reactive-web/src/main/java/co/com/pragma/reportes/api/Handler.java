package co.com.pragma.reportes.api;

import co.com.pragma.reportes.api.common.ResponseUtil;
import co.com.pragma.reportes.api.facade.LoansReportFacade;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private static final String MDC_KEY = "requestId";
    private final LoansReportFacade loansReportFacade;

    public Mono<ServerResponse> getReportApprovedLoans(ServerRequest request) {
        return Mono.defer(() -> {
            String requestId = MDC.get(MDC_KEY);
            log.info("[{}] GET /api/v1/reportes", requestId);
            return loansReportFacade.getTotalApprovedLoansReport()
                    .doOnSuccess(report -> log.info("[{}] Reporte generado: {}", requestId, report.reportType()))
                    .doOnError(error -> log.error("[{}] Error: {}", requestId, error.toString()))
                    .flatMap(report -> ResponseUtil.ok(request, "Reporte generado exitosamente", report ));
        });
    }
}
