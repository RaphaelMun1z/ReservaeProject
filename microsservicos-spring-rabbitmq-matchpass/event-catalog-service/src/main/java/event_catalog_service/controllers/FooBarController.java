package event_catalog_service.controllers;

import event_catalog_service.controllers.contracts.FooBarContract;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FooBarController implements FooBarContract {

    private final Logger logger = LoggerFactory.getLogger(FooBarController.class);

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackMethod")
    @Retry(name = "foo-bar", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "default")
    @Bulkhead(name = "default")
    public String fooBar() {
        logger.info("Request to foo-bar is received!");
        //throw new RuntimeException("Erro proposital");
        return "foo-bar";
    }

    public String fallbackMethod(Exception ex) {
        return "fallbackMethod foo-bar";
    }
}