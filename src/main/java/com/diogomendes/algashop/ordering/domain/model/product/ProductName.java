package com.diogomendes.algashop.ordering.domain.model.product;

import static com.diogomendes.algashop.ordering.domain.model.FieldValidations.requireNonBlank;

public record ProductName(String value) {

    public ProductName {
        requireNonBlank(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
