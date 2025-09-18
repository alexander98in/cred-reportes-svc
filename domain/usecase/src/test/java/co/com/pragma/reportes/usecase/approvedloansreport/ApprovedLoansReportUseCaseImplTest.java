package co.com.pragma.reportes.usecase.approvedloansreport;

import co.com.pragma.reportes.model.approvedloansreport.ApprovedLoansReport;
import co.com.pragma.reportes.model.approvedloansreport.gateways.ApprovedLoansReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApprovedLoansReportUseCaseImplTest {

    @Mock
    private ApprovedLoansReportRepository repository;

    @InjectMocks
    private ApprovedLoansReportUseCaseImpl useCase;

    private ApprovedLoansReport sampleReport;

    @BeforeEach
    void setUp() {
        sampleReport = ApprovedLoansReport.builder()
                .tipoReporte("APROBADA")
                .periodo("TOTAL")
                .count(10L)
                .totalAmount(new BigDecimal("12345.67"))
                .build();
    }

    // -------- getTotalApprovedLoansReport --------

    @Test
    void getTotalApprovedLoansReport_Success() {
        when(repository.getApprovedLoansReport(anyString(), anyString()))
                .thenReturn(Mono.just(sampleReport));

        Mono<ApprovedLoansReport> result = useCase.getTotalApprovedLoansReport();

        StepVerifier.create(result)
                .expectNext(sampleReport)
                .verifyComplete();

        // Verifica que se llamó con los valores esperados
        verify(repository).getApprovedLoansReport(eq("APROBADA"), eq("TOTAL"));
    }

    @Test
    void getTotalApprovedLoansReport_Empty() {
        when(repository.getApprovedLoansReport(anyString(), anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.getTotalApprovedLoansReport())
                .verifyComplete();

        verify(repository).getApprovedLoansReport(eq("APROBADA"), eq("TOTAL"));
    }

    @Test
    void getTotalApprovedLoansReport_Error() {
        when(repository.getApprovedLoansReport(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("DB down")));

        StepVerifier.create(useCase.getTotalApprovedLoansReport())
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        "DB down".equals(ex.getMessage()))
                .verify();

        verify(repository).getApprovedLoansReport(eq("APROBADA"), eq("TOTAL"));
    }

    // -------- addApprovedLoan --------

    @Test
    void addApprovedLoan_Success() {
        Long inc = 1L;
        BigDecimal amount = new BigDecimal("1000");

        // Capturamos el ApprovedLoansReport que el use case construye
        ArgumentCaptor<ApprovedLoansReport> captor = ArgumentCaptor.forClass(ApprovedLoansReport.class);
        when(repository.increment(any(ApprovedLoansReport.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.addApprovedLoan(inc, amount))
                .verifyComplete();

        verify(repository).increment(captor.capture());
        ApprovedLoansReport sent = captor.getValue();
        assertThat(sent.getTipoReporte()).isEqualTo("APROBADA");
        assertThat(sent.getPeriodo()).isEqualTo("TOTAL");
        assertThat(sent.getCount()).isEqualTo(inc);
        assertThat(sent.getTotalAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void addApprovedLoan_Error() {
        Long inc = 2L;
        BigDecimal amount = new BigDecimal("2500");

        when(repository.increment(any(ApprovedLoansReport.class)))
                .thenReturn(Mono.error(new IllegalStateException("write failed")));

        StepVerifier.create(useCase.addApprovedLoan(inc, amount))
                .expectErrorMatches(ex -> ex instanceof IllegalStateException &&
                        "write failed".equals(ex.getMessage()))
                .verify();

        verify(repository).increment(any(ApprovedLoansReport.class));
    }

    @Test
    void addApprovedLoan_AllowsNullAmount_PassesThrough() {
        Long inc = 3L;
        BigDecimal amount = null;

        ArgumentCaptor<ApprovedLoansReport> captor = ArgumentCaptor.forClass(ApprovedLoansReport.class);
        when(repository.increment(any(ApprovedLoansReport.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.addApprovedLoan(inc, amount))
                .verifyComplete();

        verify(repository).increment(captor.capture());
        ApprovedLoansReport sent = captor.getValue();
        assertThat(sent.getTipoReporte()).isEqualTo("APROBADA");
        assertThat(sent.getPeriodo()).isEqualTo("TOTAL");
        assertThat(sent.getCount()).isEqualTo(inc);
        assertThat(sent.getTotalAmount()).isNull(); // pasa null tal cual al repo
    }
}
