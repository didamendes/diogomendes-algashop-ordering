package com.diogomendes.algashop.ordering.domain.valueobject;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public record BirthDate(LocalDate value) {
    public BirthDate(LocalDate value) {
        requireNonNull(value);

        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }

        this.value = value;
    }

    public Integer age() {
        return LocalDate.now().getYear() - this.value.getYear();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
