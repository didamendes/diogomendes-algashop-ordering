package com.diogomendes.algashop.ordering.application.order.management;

import com.diogomendes.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import com.diogomendes.algashop.ordering.domain.model.order.*;
import com.diogomendes.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderStatus.*;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.anOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class OrderManagementApplicationServiceIT {

    @Autowired
    private OrderManagementApplicationService service;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @MockitoSpyBean
    private CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;

    @BeforeEach
    public void setup() {
        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(existingCustomer().build());
        }
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        Order order = anOrder().status(PLACED).build();
        orders.add(order);

        service.cancel(order.id().toString());

        Optional<Order> updatedOrder = orders.ofId(order.id());
        assertThat(updatedOrder).isPresent();
        assertThat(updatedOrder.get().status()).isEqualTo(CANCELED);
        assertThat(updatedOrder.get().canceledAt()).isNotNull();

        verify(orderEventListener).listen(any(OrderCanceledEvent.class));
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenCancellingNonExistingOrder() {
        String nonExistingOrderId = new OrderId().toString();

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> service.cancel(nonExistingOrderId));
    }

    @Test
    void shouldThrowOrderStatusCannotBeChangedExceptionWhenCancellingAlreadyCanceledOrder() {
        Order order = anOrder().status(CANCELED).build();
        orders.add(order);

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.cancel(order.id().toString()));
    }

    @Test
    void shouldMarkOrderAsPaidSuccessfully() {
        Order order = anOrder().status(PLACED).build();
        orders.add(order);

        service.markAsPaid(order.id().toString());

        Optional<Order> updatedOrder = orders.ofId(order.id());
        assertThat(updatedOrder).isPresent();
        assertThat(updatedOrder.get().status()).isEqualTo(PAID);
        assertThat(updatedOrder.get().paidAt()).isNotNull();

        verify(orderEventListener).listen(any(OrderPaidEvent.class));
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenMarkingNonExistingOrderAsPaid() {
        String nonExistingOrderId = new OrderId().toString();

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> service.markAsPaid(nonExistingOrderId));
    }

    @Test
    void shouldThrowOrderStatusCannotBeChangedExceptionWhenMarkingAlreadyPaidOrderAsPaid() {
        Order order = anOrder().status(PAID).build();
        orders.add(order);

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.markAsPaid(order.id().toString()));
    }

    @Test
    void shouldThrowOrderStatusCannotBeChangedExceptionWhenMarkingCanceledOrderAsPaid() {
        Order order = anOrder().status(CANCELED).build();
        orders.add(order);

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.markAsPaid(order.id().toString()));
    }

    @Test
    void shouldMarkOrderAsReadySuccessfully() {
        Order order = anOrder().status(PAID).build();
        orders.add(order);

        service.markAsReady(order.id().toString());

        Optional<Order> updatedOrder = orders.ofId(order.id());
        assertThat(updatedOrder).isPresent();
        assertThat(updatedOrder.get().status()).isEqualTo(READY);
        assertThat(updatedOrder.get().readyAt()).isNotNull();

        verify(orderEventListener).listen(any(OrderReadyEvent.class));
        verify(loyaltyPointsApplicationService).addLoyaltyPoints(
                any(UUID.class), any(String.class));
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenMarkingNonExistingOrderAsReady() {
        String nonExistingOrderId = new OrderId().toString();

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> service.markAsReady(nonExistingOrderId));
    }

    @Test
    void shouldThrowOrderStatusCannotBeChangedExceptionWhenMarkingAlreadyReadyOrderAsReady() {
        Order order = anOrder().status(READY).build();
        orders.add(order);

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.markAsReady(order.id().toString()));
    }

    @Test
    void shouldThrowOrderStatusCannotBeChangedExceptionWhenMarkingPlacedOrderAsReady() {
        Order order = anOrder().status(PLACED).build();
        orders.add(order);

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.markAsReady(order.id().toString()));
    }

}