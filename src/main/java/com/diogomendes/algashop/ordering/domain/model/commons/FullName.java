package com.diogomendes.algashop.ordering.domain.model.commons;

import static java.util.Objects.requireNonNull;

public record FullName(String firstName, String lastName) {
    public FullName(String firstName, String lastName) {
        requireNonNull(firstName);
        requireNonNull(lastName);

        if (firstName.isBlank()) {
            throw new IllegalArgumentException();
        }

        if (lastName.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
