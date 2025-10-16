package com.diogomendes.algashop.ordering.domain.model.repository;

import com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.diogomendes.algashop.ordering.domain.model.entity.Order;
import com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Money;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.diogomendes.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.diogomendes.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.entity.OrderStatus.*;
import static com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder.anOrder;
import static com.diogomendes.algashop.ordering.domain.model.valueobject.Money.ZERO;
import static java.time.Year.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Import({OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
class OrdersIT {

    private Orders orders;
    private Customers customers;

    @Autowired
    public OrdersIT(Orders orders, Customers customers) {
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    public void setup() {
        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(existingCustomer().build());
        }
    }

    @Test
    public void shouldPersistAndFind() {
        Order originalOrder = anOrder().build();
        OrderId orderId = originalOrder.id();
        orders.add(originalOrder);

        Optional<Order> possibleOrder = orders.ofId(orderId);

        assertThat(possibleOrder).isPresent();

        Order savedOrder = possibleOrder.get();

        assertThat(savedOrder).satisfies(
                s -> assertThat(s.id()).isEqualTo(orderId),
                s -> assertThat(s.customerId()).isEqualTo(originalOrder.customerId()),
                s -> assertThat(s.totalAmount()).isEqualTo(originalOrder.totalAmount()),
                s -> assertThat(s.totalItems()).isEqualTo(originalOrder.totalItems()),
                s -> assertThat(s.placedAt()).isEqualTo(originalOrder.placedAt()),
                s -> assertThat(s.paidAt()).isEqualTo(originalOrder.paidAt()),
                s -> assertThat(s.canceledAt()).isEqualTo(originalOrder.canceledAt()),
                s -> assertThat(s.readyAt()).isEqualTo(originalOrder.readyAt()),
                s -> assertThat(s.status()).isEqualTo(originalOrder.status()),
                s -> assertThat(s.paymentMethod()).isEqualTo(originalOrder.paymentMethod())
        );
    }

    @Test
    public void shouldUpdateExistingOrder() {
        Order order = anOrder().status(PLACED).build();
        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();
        order.markAsPaid();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        assertThat(order.isPaid()).isTrue();
    }

    @Test
    public void shouldNotAllowStaleUpdates() {
        Order order = anOrder().status(PLACED).build();
        orders.add(order);

        Order orderT1 = orders.ofId(order.id()).orElseThrow();
        Order orderT2 = orders.ofId(order.id()).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);

        orderT2.cancel();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(()-> orders.add(orderT2));

        Order savedOrder = orders.ofId(order.id()).orElseThrow();

        assertThat(savedOrder.canceledAt()).isNull();
        assertThat(savedOrder.paidAt()).isNotNull();

    }

    @Test
    public void shouldCountExistingOrders() {
        assertThat(orders.count()).isZero();

        Order order1 = anOrder().build();
        Order order2 = anOrder().build();

        orders.add(order1);
        orders.add(order2);

        assertThat(orders.count()).isEqualTo(2L);
    }

    @Test
    public void shouldReturnIfOrderExists() {
        Order order = anOrder().build();
        orders.add(order);

        assertThat(orders.exists(order.id())).isTrue();
        assertThat(orders.exists(new OrderId())).isFalse();
    }

    @Test
    public void shouldListExistingOrdersByYear() {
        Order order1 = anOrder().status(PLACED).build();
        Order order2 = anOrder().status(PLACED).build();
        Order order3 = anOrder().status(CANCELED).build();
        Order order4 = anOrder().status(DRAFT).build();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        CustomerId customerId = DEFAULT_CUSTOMER_ID;

        List<Order> listedOrders = orders.placedByCustomerInYear(customerId, now());

        assertThat(listedOrders).isNotEmpty();
        assertThat(listedOrders.size()).isEqualTo(2);

        listedOrders = orders.placedByCustomerInYear(customerId, now().minusYears(1));

        assertThat(listedOrders).isEmpty();

        listedOrders = orders.placedByCustomerInYear(new CustomerId(), now());

        assertThat(listedOrders).isEmpty();
    }

    @Test
    public void shouldReturnTotalSoldByCustomer() {
        Order order1 = anOrder().status(PAID).build();
        Order order2 = anOrder().status(PAID).build();

        orders.add(order1);
        orders.add(order2);

        orders.add(
                anOrder().status(CANCELED).build()
        );

        orders.add(
                anOrder().status(PLACED).build()
        );

        CustomerId customerId = DEFAULT_CUSTOMER_ID;
        Money expectedTotalAmount = order1.totalAmount().add(order2.totalAmount());

        assertThat(orders.totalSoldForCustomer(customerId)).isEqualTo(expectedTotalAmount);

        assertThat(orders.totalSoldForCustomer(new CustomerId())).isEqualTo(ZERO);
    }

    @Test
    public void shouldReturnSalesQuantityByCustomer() {
        Order order1 = anOrder().status(PAID).build();
        Order order2 = anOrder().status(PAID).build();

        orders.add(order1);
        orders.add(order2);

        orders.add(
                anOrder().status(CANCELED).build()
        );

        orders.add(
                anOrder().status(PLACED).build()
        );

        CustomerId customerId = DEFAULT_CUSTOMER_ID;

        assertThat(orders.salesQuantityByCustomerInYear(customerId, now())).isEqualTo(2L);
        assertThat(orders.salesQuantityByCustomerInYear(customerId, now().minusYears(1))).isZero();

    }

}