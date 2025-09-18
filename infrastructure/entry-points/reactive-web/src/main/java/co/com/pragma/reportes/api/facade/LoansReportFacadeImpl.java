package co.com.pragma.reportes.api.facade;

import co.com.pragma.reportes.api.dto.response.LoansReportResponseDTO;
import co.com.pragma.reportes.api.mapper.LoansReportDTOMapper;
import co.com.pragma.reportes.usecase.approvedloansreport.ApprovedLoansReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoansReportFacadeImpl implements LoansReportFacade {

    private final ApprovedLoansReportUseCase approvedLoansReportUseCase;
    private final LoansReportDTOMapper mapper;

    @Override
    public Mono<LoansReportResponseDTO> getTotalApprovedLoansReport() {
        return Mono.defer(() ->
                approvedLoansReportUseCase.getTotalApprovedLoansReport()
                    .map(mapper::toResponse)
        );
    }
}
