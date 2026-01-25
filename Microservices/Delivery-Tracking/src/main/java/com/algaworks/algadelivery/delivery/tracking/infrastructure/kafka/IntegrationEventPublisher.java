package com.algaworks.algadelivery.delivery.tracking.infrastructure.kafka;

public interface IntegrationEventPublisher {

    void publish(Object event, String key, String topic);
}
