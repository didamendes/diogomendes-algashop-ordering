package com.diogomendes.algashop.ordering.domain.model.valueobject;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Billing(FullName fullName,
                      Document document,
                      Phone phone,
                      Email email,
                      Address address) {

    public Billing {
        requireNonNull(fullName);
        requireNonNull(document);
        requireNonNull(phone);
        requireNonNull(email);
        requireNonNull(address);
    }

}
