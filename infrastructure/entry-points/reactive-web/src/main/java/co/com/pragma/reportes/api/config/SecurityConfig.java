package co.com.pragma.reportes.api.config;

import co.com.pragma.reportes.api.exceptions.JsonAccessDeniedHandler;
import co.com.pragma.reportes.api.exceptions.JsonAuthenticationEntryPoint;
import co.com.pragma.reportes.model.auth.TokenVerification;
import co.com.pragma.reportes.model.auth.gateways.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenService tokenService;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        var authFilter = new AuthenticationWebFilter(jwtAuthenticationManager());
        authFilter.setServerAuthenticationConverter(this::convert);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(ex -> ex
                        // CORS preflight
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        // Publicos (ej. docs)
                        .pathMatchers( "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Listar solicitudes: ADMIN y ASESOR
                        .pathMatchers(HttpMethod.GET, "/api/v1/reportes").hasAnyAuthority("ADMIN","ASESOR")
                        .anyExchange().authenticated()
                )
                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint) // 401 JSON
                        .accessDeniedHandler(jsonAccessDeniedHandler)           // 403 JSON
                )
                .build();
    }

    private Mono<Authentication> convert(ServerWebExchange exchange) {
        var authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return Mono.empty();
        var token = authHeader.substring(7);

        return tokenService.verify(token)
                .filter(TokenVerification::valid)
                .map(tv -> new UsernamePasswordAuthenticationToken(
                        tv.subject(),                // principal
                        token,                       // credentials = el JWT (lo usaremos para propagar)
                        tv.authorities().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                ));
    }

    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager() {
        // el convert ya valido; passthrough
        return authentication -> Mono.just(authentication);
    }
}
