package com.algaworks.algadelivery.courier.management.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourierPayoutCalculationInput {

    @NotNull
    private Double distanceInKm;

}
