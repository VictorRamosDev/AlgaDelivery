package com.algaworks.algadelivery.delivery.tracking.domain.model;

import com.algaworks.algadelivery.delivery.tracking.domain.exception.DomainException;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Delivery {

    @EqualsAndHashCode.Include
    private UUID id;

    private UUID courierId;

    @Setter
    private DeliveryStatus status;

    private OffsetDateTime placedAt;
    private OffsetDateTime assignedAt;
    private OffsetDateTime expectedDeliveryAt;
    private OffsetDateTime fulfilledAt;

    private BigDecimal distanceFee;
    private BigDecimal courierPayout;
    private BigDecimal totalCost;

    private Integer totalItems;

    private ContactPoint sender;
    private ContactPoint recipient;

    private List<Item> items = new ArrayList<>();

    public static Delivery draft() {
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setCourierPayout(BigDecimal.ZERO);
        delivery.setTotalCost(BigDecimal.ZERO);
        delivery.setTotalItems(0);
        delivery.setStatus(DeliveryStatus.DRAFT);
        delivery.setDistanceFee(BigDecimal.ZERO);

        return delivery;
    }

    public UUID addItem(String name, int quantity) {
        Item item = Item.brandNew(name, quantity);
        items.add(item);
        this.calculateTotalItems();

        return item.getId();
    }

    public void removeItem(UUID itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        this.calculateTotalItems();
    }

    public void removeItems() {
        items.clear();
        this.calculateTotalItems();
    }

    public void editPreparationDetails(PreparationDetails details) {
        verifyIfCanBeEdited();
        this.setSender(details.getSender());
        this.setRecipient(details.getRecipient());
        this.setDistanceFee(details.getDistanceFee());
        this.setCourierPayout(details.getCourierPayout());
        this.setExpectedDeliveryAt(OffsetDateTime.now().plus(details.getExpectedDeliveryTime()));
        setTotalCost(details.getCourierPayout().add(details.getDistanceFee()));
    }

    public void place() {
        verifyIfCanBePlaced();
        this.changeStatusTo(DeliveryStatus.WAITING_FOR_COURIER);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void pickUp(UUID courierId) {
        this.setCourierId(courierId);
        this.changeStatusTo(DeliveryStatus.IN_TRANSIT);
        this.setAssignedAt(OffsetDateTime.now());
    }

    public void markAsDelivered() {
        this.changeStatusTo(DeliveryStatus.DELIVERED);
        this.setFulfilledAt(OffsetDateTime.now());
    }

    public void changeItemQuantity(UUID itemId, int quantity) {
        Item item = this.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow();
        item.setQuantity(quantity);
        calculateTotalItems();
    }

    private void changeStatusTo(DeliveryStatus newStatus) {
        if (newStatus != null && this.getStatus().canNotChangeTo(newStatus)) {
            throw new DomainException("Invalid status transition from " + this.getStatus() + " to " + newStatus);
        }

        this.setStatus(newStatus);
    }

    private void calculateTotalItems() {
        int totalItems = this.getItems().stream().mapToInt(Item::getQuantity).sum();
        this.setTotalItems(totalItems);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    private void verifyIfCanBePlaced() {
        if (!isFilled()) {
            throw new DomainException();
        }
        if (this.getStatus() != DeliveryStatus.DRAFT) {
            throw new DomainException();
        }
    }

    private void verifyIfCanBeEdited() {
        if (!this.getStatus().equals(DeliveryStatus.DRAFT)) {
            throw new DomainException();
        }
    }

    private boolean isFilled() {
        return this.getSender() != null
                && this.getRecipient() != null
                && this.getTotalCost() != null;
    }

    @Getter
    @Builder
    public static class PreparationDetails {

        private ContactPoint sender;
        private ContactPoint recipient;
        private BigDecimal distanceFee;
        private BigDecimal courierPayout;
        private Duration expectedDeliveryTime;
    }
}
