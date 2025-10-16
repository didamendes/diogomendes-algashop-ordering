package com.diogomendes.algashop.ordering.infrastructure.persistence.provider;

import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus;
import com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.diogomendes.algashop.ordering.domain.model.repository.Customers;
import com.diogomendes.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.existingCustomer;
import static org.assertj.core.api.Assertions.*;

import static com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus.PLACED;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
class OrdersPersistenceProviderIT {

    private OrdersPersistenceProvider persistenceProvider;
    private OrderPersistenceEntityRepository entityRepository;
    private CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider persistenceProvider, CustomersPersistenceProvider customersPersistenceProvider,
                                       OrderPersistenceEntityRepository entityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.entityRepository = entityRepository;
        this.customersPersistenceProvider = customersPersistenceProvider;
    }

    @BeforeEach
    public void setup() {
        if (!customersPersistenceProvider.exists(DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(existingCustomer().build());
        }
    }

    @Test
    public void shouldUpdateAndKeepPersistenceEntityState() {
        Order order = OrderTestDataBuilder.anOrder().status(PLACED).build();
        long orderId = order.id().value().toLong();
        persistenceProvider.add(order);

        var persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        assertThat(persistenceEntity.getStatus()).isEqualTo(PLACED.name());

        assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

        order = persistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        persistenceProvider.add(order);

        persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PAID.name());

        assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

    }

    @Test
    @Transactional(propagation = NOT_SUPPORTED)
    public void shouldAddFindAndNotFailWhenNoTransaction() {
        Order order = OrderTestDataBuilder.anOrder().build();
        persistenceProvider.add(order);

        Order savedOrder = persistenceProvider.ofId(order.id()).orElseThrow();

        assertThat(savedOrder).isNotNull();
    }

}