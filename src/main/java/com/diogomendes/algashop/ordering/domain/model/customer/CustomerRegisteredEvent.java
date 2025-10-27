package com.diogomendes.algashop.ordering.domain.model.customer;

import com.diogomendes.algashop.ordering.domain.model.commons.Email;
import com.diogomendes.algashop.ordering.domain.model.commons.FullName;

import java.time.OffsetDateTime;

public record CustomerRegisteredEvent(
        CustomerId customerId,
        OffsetDateTime registeredAt,
        FullName fullName,
        Email email
) {
}
