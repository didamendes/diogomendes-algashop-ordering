package com.diogomendes.algashop.ordering.domain.valueobject;

import static com.diogomendes.algashop.ordering.domain.validator.FieldValidations.requireNonBlank;

public record ProductName(String value) {

    public ProductName {
        requireNonBlank(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
