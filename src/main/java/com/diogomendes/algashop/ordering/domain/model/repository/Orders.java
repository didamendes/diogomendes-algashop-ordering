package com.diogomendes.algashop.ordering.domain.model.repository;

import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderId;

public interface Orders extends Repository<Order, OrderId> {
}
