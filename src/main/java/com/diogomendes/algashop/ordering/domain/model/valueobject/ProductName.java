package com.diogomendes.algashop.ordering.domain.model.valueobject;

import static com.diogomendes.algashop.ordering.domain.model.validator.FieldValidations.requireNonBlank;

public record ProductName(String value) {

    public ProductName {
        requireNonBlank(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
