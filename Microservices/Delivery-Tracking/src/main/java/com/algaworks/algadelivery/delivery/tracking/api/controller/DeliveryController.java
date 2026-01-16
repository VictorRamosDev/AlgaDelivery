package com.algaworks.algadelivery.delivery.tracking.api.controller;

import com.algaworks.algadelivery.delivery.tracking.api.model.DeliveryInput;
import com.algaworks.algadelivery.delivery.tracking.domain.model.Delivery;
import com.algaworks.algadelivery.delivery.tracking.domain.repository.DeliveryRepository;
import com.algaworks.algadelivery.delivery.tracking.domain.service.DeliveryPreparationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryPreparationService deliveryPreparationService;

    private final DeliveryRepository deliveryRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Delivery draft(@RequestBody @Valid DeliveryInput request) {
        System.out.println("Validando criação de rascunho de entrega...");
        System.out.println("Funcionando.");
        return deliveryPreparationService.draft(request);
    }

    @PutMapping("/{deliveryId}")
    public Delivery edit(@PathVariable UUID deliveryId, @RequestBody @Valid DeliveryInput request) {
        return deliveryPreparationService.edit(request, deliveryId);
    }

    @GetMapping
    public PagedModel<Delivery> findAll(Pageable pageable) {
        return new PagedModel<>(deliveryRepository.findAll(pageable));
    }

    @GetMapping("/{deliveryId}")
    public Delivery findById(@PathVariable UUID deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
