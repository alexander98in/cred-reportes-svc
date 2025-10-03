package co.com.pragma.reportes.dynamodb.aws;

import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import co.com.pragma.reportes.model.approvedloansreport.gateways.TaskQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SqsTaskQueueAdapter implements TaskQueue {

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.task-queue.url}")
    private String queueUrl;

    @Value("${aws.sqs.task-queue.is-fifo:false}")
    private boolean isFifo;

    @Override
    public Mono<Void> publishTask(ApprovedLoansReport event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(json -> {
                    SendMessageRequest.Builder builder = SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(json);

                    if (isFifo) {
                        // Para colas FIFO se requiere MessageGroupId y (opcional) DeduplicationId
                        builder = builder
                                .messageGroupId(event.getCount() != null ? event.getPeriodo() : "default-group")
                                .messageDeduplicationId(UUID.randomUUID().toString());
                    }
                    return Mono.fromFuture(sqsAsyncClient.sendMessage(builder.build())).then();
                });
    }
}
