package event_catalog_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Foo Bar Endpoint", description = "Endpoint de testes para simulação e validação de resiliência (Resilience4j)")
@RequestMapping("/event-catalog-service/api/event/validate")
public interface FooBarContract {

    @Operation(summary = "Endpoint de teste para testar padrões de falha como Bulkhead, Rate Limiter e Circuit Breaker")
    @GetMapping("/foo-bar")
    String fooBar();
}