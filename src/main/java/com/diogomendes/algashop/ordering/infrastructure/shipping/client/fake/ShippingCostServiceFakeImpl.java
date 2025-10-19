package com.diogomendes.algashop.ordering.infrastructure.shipping.client.fake;

import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static java.time.LocalDate.now;

@Component
@ConditionalOnProperty(name = "diogoshop.integrations.shipping.provider", havingValue = "FAKE")
public class ShippingCostServiceFakeImpl implements ShippingCostService {
    @Override
    public CalculationResult calculate(CalculationRequest request) {
        return new CalculationResult(
                new Money("20"),
                now().plusDays(5)
        );
    }
}
