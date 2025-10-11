package com.diogomendes.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record ZipCode(String value) {
    public ZipCode {
        Objects.nonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }

        if (value.length() != 5) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
