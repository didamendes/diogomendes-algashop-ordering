package com.diogomendes.algashop.ordering.domain.model.valueobject;

import static com.diogomendes.algashop.ordering.domain.model.validator.FieldValidations.requiredValidEmail;
import static java.util.Objects.requireNonNull;

public record Email(String value) {

    public Email(String value) {
        requireNonNull(value);
        requiredValidEmail(value);

        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
