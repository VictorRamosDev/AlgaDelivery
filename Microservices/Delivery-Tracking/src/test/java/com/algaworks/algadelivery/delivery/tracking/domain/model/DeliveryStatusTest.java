package com.algaworks.algadelivery.delivery.tracking.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusTest {

    @Test
    public void draft_shouldChangeToWaitingForCourier() {
        boolean status = DeliveryStatus.DRAFT.canChangeTo(DeliveryStatus.WAITING_FOR_COURIER);

        assertTrue(status);
    }

    @Test
    public void draft_canNotChangeToInTransit() {
        boolean status = DeliveryStatus.DRAFT.canNotChangeTo(DeliveryStatus.IN_TRANSIT);

        assertTrue(status);
    }

}