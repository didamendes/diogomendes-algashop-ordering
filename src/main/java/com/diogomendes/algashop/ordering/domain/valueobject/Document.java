package com.diogomendes.algashop.ordering.domain.valueobject;

import static java.util.Objects.requireNonNull;

public record Document(String value) {

    public Document(String value) {
        requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
