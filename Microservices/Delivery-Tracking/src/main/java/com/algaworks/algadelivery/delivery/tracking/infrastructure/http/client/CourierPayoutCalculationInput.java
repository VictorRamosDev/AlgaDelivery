package com.algaworks.algadelivery.delivery.tracking.infrastructure.http.client;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourierPayoutCalculationInput {

    @NotNull
    private Double distanceInKm;

}
