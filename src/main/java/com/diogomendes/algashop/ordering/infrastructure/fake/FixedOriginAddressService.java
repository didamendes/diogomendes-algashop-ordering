package com.diogomendes.algashop.ordering.infrastructure.fake;

import com.diogomendes.algashop.ordering.domain.model.service.OriginAddressService;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Address;
import com.diogomendes.algashop.ordering.domain.model.valueobject.ZipCode;
import org.springframework.stereotype.Component;

@Component
public class FixedOriginAddressService implements OriginAddressService {
    @Override
    public Address originAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("North Ville")
                .city("York")
                .state("South Calofornia")
                .zipCode(new ZipCode("12345"))
                .build();
    }
}
