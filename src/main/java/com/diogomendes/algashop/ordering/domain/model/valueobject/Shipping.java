package com.diogomendes.algashop.ordering.domain.model.valueobject;

import lombok.Builder;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

@Builder(toBuilder = true)
public record Shipping(Recipient recipient,
                       Address address,
                       Money cost,
                       LocalDate expectedDate) {

    public Shipping {
        requireNonNull(recipient);
        requireNonNull(address);
        requireNonNull(cost);
        requireNonNull(expectedDate);
    }

}
