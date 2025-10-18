package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.service.ShippingCostService.CalculationRequest;
import com.diogomendes.algashop.ordering.domain.model.service.ShippingCostService.CalculationResult;
import com.diogomendes.algashop.ordering.domain.model.valueobject.ZipCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShippingCostServiceIT {

    @Autowired
    private ShippingCostService shippingCostService;

    @Autowired
    private OriginAddressService originAddressService;

    @Test
    void shouldCalculate() {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode("12345");

        CalculationResult calculate = shippingCostService.calculate(new CalculationRequest(origin, destination));

        assertThat(calculate.cost()).isNotNull();
        assertThat(calculate.expectedDate()).isNotNull();
    }

}