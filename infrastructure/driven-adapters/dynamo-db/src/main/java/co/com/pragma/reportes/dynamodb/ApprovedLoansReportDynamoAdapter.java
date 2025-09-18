package co.com.pragma.reportes.dynamodb;

import co.com.pragma.reportes.dynamodb.entity.ApprovedLoansReportEntity;
import co.com.pragma.reportes.dynamodb.helper.TemplateAdapterOperations;
import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import co.com.pragma.reportes.model.approvedloansreport.gateways.ApprovedLoansReportRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public class ApprovedLoansReportDynamoAdapter
    extends TemplateAdapterOperations<ApprovedLoansReport, String, ApprovedLoansReportEntity>
    implements ApprovedLoansReportRepository  {

    private final DynamoDbAsyncClient dynamo;
    private final String tableName;

    public ApprovedLoansReportDynamoAdapter(
            DynamoDbEnhancedAsyncClient enhancedClient,
            ObjectMapper mapper,
            DynamoDbAsyncClient dynamo,
            @Value("${app.dynamodb.table.reporteAprobados}") String tableName
    ) {
        super(enhancedClient, mapper,
                entity -> mapper.map(entity, ApprovedLoansReport.class),
                "reporte_aprobados"
        );
        this.dynamo = dynamo;
        this.tableName = tableName;
    }

    @Override
    public Mono<ApprovedLoansReport> getApprovedLoansReport(String tipoReporte, String periodo) {
        return getByPkSk(tipoReporte, periodo);
    }

    @Override
    public Mono<Void> increment(ApprovedLoansReport approvedLoansReport) {
        String tipoReporte = approvedLoansReport.getTipoReporte();
        String periodo = approvedLoansReport.getPeriodo();
        long incCount = approvedLoansReport.getCount();
        BigDecimal incAmount = approvedLoansReport.getTotalAmount();

        var req = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "tipoReporte", AttributeValue.builder().s(tipoReporte).build(),
                        "periodo", AttributeValue.builder().s(periodo).build()
                ))
                .updateExpression("ADD #c :incC, #t :incT")
                .expressionAttributeNames(Map.of(
                        "#c", "count",
                        "#t", "totalAmount"
                ))
                .expressionAttributeValues(Map.of(
                        ":incC", AttributeValue.builder().n(Long.toString(incCount)).build(),
                        ":incT", AttributeValue.builder().n(incAmount.toPlainString()).build()
                ))
                .build();

        return Mono.fromFuture(() -> dynamo.updateItem(req)).then();
    }
}
