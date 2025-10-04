package com.diogomendes.algashop.ordering.domain.exception;

import com.diogomendes.algashop.ordering.domain.valueobject.id.OrderId;
import com.diogomendes.algashop.ordering.domain.valueobject.id.OrderItemId;

import static com.diogomendes.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(OrderId id, OrderItemId orderItemId) {
        super(String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM, id, orderItemId));
    }

}
