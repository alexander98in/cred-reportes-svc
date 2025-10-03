package co.com.pragma.reportes.api.scheduler;

import co.com.pragma.reportes.usecase.approvedloansreport.ApprovedLoansReportUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovedLoansReportJob {

    @Value("${app.scheduling.zone:America/Bogota}")
    private String zone;

    private final ApprovedLoansReportUseCase approvedLoansReportUseCase;

    @Scheduled(cron = "${app.scheduling.hello-cron}", zone = "${app.scheduling.zone:America/Bogota}")
    public void sendTask() {
        log.info("Iniciando tarea programada para enviar reporte de créditos aprobados...");

        approvedLoansReportUseCase.sendApprovedLoanReport()
                .doOnSubscribe(sub -> log.info("Ejecutando sendApprovedLoanReport()..."))
                .doOnSuccess(v -> log.info("Reporte enviado exitosamente"))
                .doOnError(error -> log.error("Error al enviar el reporte de créditos aprobados", error))
                .subscribe();
    }

}
