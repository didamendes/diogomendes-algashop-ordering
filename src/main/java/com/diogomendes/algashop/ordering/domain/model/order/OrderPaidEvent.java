package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record OrderPaidEvent(
        OrderId orderId,
        CustomerId customerId,
        OffsetDateTime paidAt
) {
}
