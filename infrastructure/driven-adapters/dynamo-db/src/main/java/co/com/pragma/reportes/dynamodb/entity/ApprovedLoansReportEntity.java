package co.com.pragma.reportes.dynamodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedLoansReportEntity {

    private String tipoReporte;
    private String periodo;
    private Long count;
    private BigDecimal totalAmount;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("tipoReporte")
    public String getTipoReporte() { return tipoReporte; }

    @DynamoDbSortKey
    @DynamoDbAttribute("periodo")
    public String getPeriodo() { return periodo; }
}
