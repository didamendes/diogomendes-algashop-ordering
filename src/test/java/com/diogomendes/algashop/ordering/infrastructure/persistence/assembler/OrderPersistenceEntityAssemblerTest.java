package com.diogomendes.algashop.ordering.infrastructure.persistence.assembler;

import com.diogomendes.algashop.ordering.domain.model.order.Order;
import com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.diogomendes.algashop.ordering.domain.model.order.OrderItemId;
import com.diogomendes.algashop.ordering.infrastructure.persistence.order.OrderItemPersistenceEntity;
import com.diogomendes.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.diogomendes.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.diogomendes.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @InjectMocks
    private OrderPersistenceEntityAssembler assembler;

    @BeforeEach
    public void setup() {
        when(customerPersistenceEntityRepository.getReferenceById(any(UUID.class)))
                .then(((a) -> aCustomer().id(a.getArgument(0, UUID.class)).build()));
    }

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