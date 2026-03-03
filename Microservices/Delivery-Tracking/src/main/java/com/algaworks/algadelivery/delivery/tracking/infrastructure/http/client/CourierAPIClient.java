package com.algaworks.algadelivery.delivery.tracking.infrastructure.http.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/v1/couriers")
public interface CourierAPIClient {

    @CircuitBreaker(name = "CircuitBraker_CourierAPIClient_payoutCalculation")
    @Retry(name = "Retry_CourierAPIClient_payoutCalculation")
    @PostExchange("/payout-calculation")
    CourierPayoutResultModel payoutCalculation(@RequestBody CourierPayoutCalculationInput input);
}
