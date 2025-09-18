package co.com.pragma.reportes.model.approvedloansreport;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApprovedLoansReport {

    String tipoReporte;
    String periodo;
    Long count;
    BigDecimal totalAmount;
}
