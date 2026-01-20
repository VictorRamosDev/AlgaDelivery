package com.algaworks.algadelivery.courier.management.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CourierPayoutResultModel {

    private BigDecimal payout;
}
