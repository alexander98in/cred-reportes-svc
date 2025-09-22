package co.com.pragma.reportes.api.config;

import co.com.pragma.reportes.api.dto.response.LoansReportResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRestDocs {
    private  final RouterFunction<ServerResponse> routerRest;

    public RouterRestDocs(@Qualifier("routerFunction") RouterFunction<ServerResponse> routerRest) {
        this.routerRest = routerRest;
    }

    @Bean("routerRestOpenApi")
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reportes",
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    beanMethod = "generateReport",
                    operation = @Operation(
                            operationId = "generateReport",
                            tags = { "Reportes" },
                            summary = "Generar reporte",
                            description = "Genera un reporte basado en los prestamos aprobados",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Reporte generado exitosamente",
                                    content = @Content(
                                            schema = @Schema(
                                                    implementation = LoansReportResponseDTO.class
                                            )
                                    )
                            )
                    )
            )
    })
    public RouterFunction<ServerResponse> routerRestOpenApi() {
        return this.routerRest;
    }
}
