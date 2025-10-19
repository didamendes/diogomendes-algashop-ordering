package com.diogomendes.algashop.ordering.domain.model.commons;

import lombok.Builder;

import static com.diogomendes.algashop.ordering.domain.model.FieldValidations.requireNonBlank;
import static java.util.Objects.requireNonNull;

public record Address(
        String street,
        String complement,
        String neighborhood,
        String number,
        String city,
        String state,
        ZipCode zipCode
) {

    @Builder(toBuilder = true)
    public Address {
        requireNonBlank(street);
        requireNonBlank(neighborhood);
        requireNonBlank(city);
        requireNonBlank(number);
        requireNonBlank(state);
        requireNonNull(zipCode);
    }
}
