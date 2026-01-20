package com.algaworks.algadelivery.delivery.tracking.domain.service;

import com.algaworks.algadelivery.delivery.tracking.api.model.ContactPointInput;
import com.algaworks.algadelivery.delivery.tracking.api.model.DeliveryInput;
import com.algaworks.algadelivery.delivery.tracking.api.model.ItemInput;
import com.algaworks.algadelivery.delivery.tracking.domain.exception.DomainException;
import com.algaworks.algadelivery.delivery.tracking.domain.model.ContactPoint;
import com.algaworks.algadelivery.delivery.tracking.domain.model.Delivery;
import com.algaworks.algadelivery.delivery.tracking.domain.model.DeliveryEstimate;
import com.algaworks.algadelivery.delivery.tracking.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryPreparationService {

    private final DeliveryRepository deliveryRepository;

    private final DeliveryTimeEstimationService deliveryTimeEstimationService;

    private final CourierPayoutCalculationService courierPayoutCalculationService;

    @Transactional
    public Delivery draft(DeliveryInput input) {
        Delivery delivery = Delivery.draft();
        this.handlePreparation(delivery, input);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery edit(DeliveryInput input, UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new DomainException());
        delivery.removeItems(); //Adicionado apenas para facilitar, mas o ideal seria um de-para para validar quais items foram alterados
        handlePreparation(delivery, input);

        return deliveryRepository.save(delivery);
    }

    public void handlePreparation(Delivery delivery, DeliveryInput input) {
        ContactPointInput senderInput = input.getSender();
        ContactPointInput recipientInput = input.getRecipient();

        ContactPoint sender = ContactPoint.builder()
                .zipCode(senderInput.getZipCode())
                .street(senderInput.getStreet())
                .number(senderInput.getNumber())
                .complement(senderInput.getComplement())
                .name(senderInput.getName())
                .phone(senderInput.getPhone())
                .build();

        ContactPoint recipient = ContactPoint.builder()
                .zipCode(recipientInput.getZipCode())
                .street(recipientInput.getStreet())
                .number(recipientInput.getNumber())
                .complement(recipientInput.getComplement())
                .name(recipientInput.getName())
                .phone(recipientInput.getPhone())
                .build();

        DeliveryEstimate deliveryEstimate = deliveryTimeEstimationService.estimate(sender, recipient);
        BigDecimal distanceFee = this.calculateDistanceFee(deliveryEstimate);
        BigDecimal payout = courierPayoutCalculationService.calculatePayout(deliveryEstimate.getDistanceInKm());

        var preparationDetails = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(distanceFee)
                .courierPayout(payout)
                .expectedDeliveryTime(deliveryEstimate.getEstimatedTime())
                .build();

        delivery.editPreparationDetails(preparationDetails);

        for (ItemInput itemInput : input.getItems()) {
            delivery.addItem(itemInput.getName(), itemInput.getQuantity());
        }

    }

    private BigDecimal calculateDistanceFee(DeliveryEstimate deliveryEstimate) {
        return BigDecimal.valueOf(3.0)
                .multiply(BigDecimal.valueOf(deliveryEstimate.getDistanceInKm()))
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
