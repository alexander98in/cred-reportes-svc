package co.com.pragma.reportes.api.facade;

import co.com.pragma.reportes.api.dto.response.LoansReportResponseDTO;
import reactor.core.publisher.Mono;

public interface LoansReportFacade {

    Mono<LoansReportResponseDTO> getTotalApprovedLoansReport();
}
