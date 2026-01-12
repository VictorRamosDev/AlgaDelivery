package com.algaworks.algadelivery.courier.management.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourierTest {

    @Test
    public void shouldAssignToCourier() {
        Courier courier = Courier.brandNew("Antonio Kelven de Sousa", "(11) 98765-4321");
        UUID uuid = UUID.randomUUID();
        AssignedDelivery delivery = AssignedDelivery.pending(uuid);

        courier.assign(uuid);

        assertEquals(uuid, delivery.getId());
        assertNotNull(delivery.getAssignedAt());
        assertEquals(1, courier.getPendingDeliveries().size());
    }

}