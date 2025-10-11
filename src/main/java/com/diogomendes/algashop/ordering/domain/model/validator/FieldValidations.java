package com.diogomendes.algashop.ordering.domain.model.validator;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.validator.routines.EmailValidator.getInstance;

public class FieldValidations {

    private FieldValidations() {}

    public static void requireNonBlank(String value) {
        requireNonBlank(value, "");
    }

    public static void requireNonBlank(String value, String errorMessage) {
        requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    public static void requiredValidEmail(String email) {
        requiredValidEmail(email, null);
    }

    public static void requiredValidEmail(String email, String errorMessage) {
        requireNonNull(email, errorMessage);

        if (email.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (!getInstance().isValid(email)) {
            throw new IllegalArgumentException(email);
        }
    }

}
