package com.diogomendes.algashop.ordering.infrastructure.client.rapidex;

import com.diogomendes.algashop.ordering.domain.model.service.ShippingCostService;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.time.LocalDate.now;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "diogoshop.integrations.shipping.provider", havingValue = "rapidex")
public class ShippingCostServiceRapidexImpl implements ShippingCostService {

    private final RapiDexAPIClient rapiDexAPIClient;

    @Override
    public CalculationResult calculate(CalculationRequest request) {
        DeliveryCostResponse response = rapiDexAPIClient.calculate
                (new DeliveryCostRequest(request.origin().value(), request.destination().value()));

        LocalDate expectedDeliveryDate = now().plusDays(response.getEstimatedDaysToDeliver());

        return CalculationResult.builder()
                .cost(new Money(response.getDeliveryCost()))
                .expectedDate(expectedDeliveryDate)
                .build();
    }
}
