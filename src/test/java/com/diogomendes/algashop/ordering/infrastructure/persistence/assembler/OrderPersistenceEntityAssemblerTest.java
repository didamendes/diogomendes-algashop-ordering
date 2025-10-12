package com.diogomendes.algashop.ordering.infrastructure.persistence.assembler;

import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceEntityAssemblerTest {

    private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

    @Test
    void shoudCConvertToDomain() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderPersistenceEntity orderPersistenceEntity = assembler.fromDomain(order);
        assertThat(orderPersistenceEntity).satisfies(
            p -> assertThat(p.getId()).isEqualTo(order.id().value().toLong()),
            p ->  assertThat(p.getCustomerId()).isEqualTo(order.customerId().value()),
                p ->  assertThat(p.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                p ->  assertThat(p.getTotalItems()).isEqualTo(order.totalItems().value()),
                p ->  assertThat(p.getStatus()).isEqualTo(order.status().name()),
                p ->  assertThat(p.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                p ->  assertThat(p.getPlacedAt()).isEqualTo(order.placedAt()),
                p ->  assertThat(p.getPaidAt()).isEqualTo(order.paidAt()),
                p ->  assertThat(p.getCanceledAt()).isEqualTo(order.canceledAt()),
                p ->  assertThat(p.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

    @Test
    void givenOrderWithNotItems_shoudRemovePersistenceEntityItems() {
        Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
        OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        assertThat(order.items()).isEmpty();
        assertThat(orderPersistenceEntity.getItems()).isNotEmpty();

        assembler.merge(orderPersistenceEntity, order);

        assertThat(orderPersistenceEntity.getItems()).isEmpty();
    }

    @Test
    void givenOrderWithItems_shoudAddToPersistenceEntity() {
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().items(new HashSet<>()).build();

        assertThat(order.items()).isNotEmpty();
        assertThat(persistenceEntity.getItems()).isEmpty();

        assembler.merge(persistenceEntity, order);

        assertThat(persistenceEntity.getItems()).isNotEmpty();
        assertThat(persistenceEntity.getItems().size()).isEqualTo(order.items().size());
    }

    @Test
    void givenOrderWithItems_whenMerge_shouldRemoveMergeCorrectly() {
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();

        assertThat(order.items().size()).isEqualTo(2);

        Set<OrderItemPersistenceEntity> itemPersistenceEntities = order.items().stream()
                .map(i -> assembler.fromDomain(i))
                .collect(Collectors.toSet());

        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .items(itemPersistenceEntities).build();

        OrderItemId ordemItem = order.items().iterator().next().id();
        order.removeItem(ordemItem);

        assembler.merge(persistenceEntity, order);

        assertThat(persistenceEntity.getItems()).isNotEmpty();
        assertThat(persistenceEntity.getItems().size()).isEqualTo(order.items().size());
    }

}