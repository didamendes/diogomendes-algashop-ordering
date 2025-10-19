package com.diogomendes.algashop.ordering.domain.model.order.shipping;

import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import com.diogomendes.algashop.ordering.domain.model.commons.ZipCode;
import lombok.Builder;

import java.time.LocalDate;

public interface ShippingCostService {
    CalculationResult calculate(CalculationRequest request);

    @Builder
    record CalculationRequest(ZipCode origin, ZipCode destination) {

    }

    @Builder
    record CalculationResult(Money cost, LocalDate expectedDate) {

    }
}
