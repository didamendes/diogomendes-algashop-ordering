package com.diogomendes.algashop.ordering.domain.model.exception;

import com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderId;

import static com.diogomendes.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED;

public class OrderCannotBeEditedException extends DomainException {
    public OrderCannotBeEditedException(OrderId id, OrderStatus status) {
        super(String.format(ERROR_ORDER_CANNOT_BE_EDITED, id, status));
    }
}
