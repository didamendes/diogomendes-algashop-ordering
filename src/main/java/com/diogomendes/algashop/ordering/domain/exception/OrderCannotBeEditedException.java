package com.diogomendes.algashop.ordering.domain.exception;

import com.diogomendes.algashop.ordering.domain.entity.OrderStatus;
import com.diogomendes.algashop.ordering.domain.valueobject.id.OrderId;

import static com.diogomendes.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED;

public class OrderCannotBeEditedException extends DomainException {
    public OrderCannotBeEditedException(OrderId id, OrderStatus status) {
        super(String.format(ERROR_ORDER_CANNOT_BE_EDITED, id, status));
    }
}
