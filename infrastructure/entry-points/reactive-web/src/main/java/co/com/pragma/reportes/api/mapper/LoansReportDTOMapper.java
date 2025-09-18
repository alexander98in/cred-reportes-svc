package co.com.pragma.reportes.api.mapper;

import co.com.pragma.reportes.api.dto.response.LoansReportResponseDTO;
import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoansReportDTOMapper {

    @Mapping(target = "reportType", source = "approvedLoansReport.tipoReporte")
    @Mapping(target = "period", source = "approvedLoansReport.periodo")
    @Mapping(target = "totalApprovedLoans", source = "approvedLoansReport.count")
    @Mapping(target = "totalAmountApprovedLoans", source = "approvedLoansReport.totalAmount")
    LoansReportResponseDTO toResponse(ApprovedLoansReport approvedLoansReport);
}
