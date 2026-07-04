package event_catalog_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Foo Bar Endpoint", description = "Endpoint de testes para simulação e validação de resiliência (Resilience4j)")
public interface FooBarContract {

    @Operation(summary = "Endpoint de teste para testar padrões de falha como Bulkhead, Rate Limiter e Circuit Breaker")
    String fooBar();
}