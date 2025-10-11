package com.diogomendes.algashop.ordering.infrastructure.persistence.repository;

import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersistenceEntityRepository extends JpaRepository<OrderPersistenceEntity, Long> {
}
