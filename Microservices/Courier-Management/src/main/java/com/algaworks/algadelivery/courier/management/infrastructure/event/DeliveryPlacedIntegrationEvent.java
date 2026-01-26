package com.algaworks.algadelivery.courier.management.infrastructure.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class DeliveryPlacedIntegrationEvent {

    private OffsetDateTime ocurredAt;

    private UUID deliveryId;
}
