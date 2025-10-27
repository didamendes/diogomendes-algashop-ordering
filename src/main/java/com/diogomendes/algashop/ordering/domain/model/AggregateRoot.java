package com.diogomendes.algashop.ordering.domain.model;

public interface AggregateRoot<ID> extends DomainEventSource {
    ID id();
}
