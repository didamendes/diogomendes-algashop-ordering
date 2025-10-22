package com.diogomendes.algashop.ordering.application.customer.loyaltypoints;

import com.diogomendes.algashop.ordering.domain.model.commons.Email;
import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import com.diogomendes.algashop.ordering.domain.model.customer.*;
import com.diogomendes.algashop.ordering.domain.model.order.*;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import io.hypersistence.tsid.TSID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.brandNewCustomer;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.customer.LoyaltyPoints.ZERO;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderStatus.*;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.anOrder;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class CustomerLoyaltyPointsApplicationServiceIT {

    @Autowired
    private CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @Test
    void shouldAddLoyaltyPointsToCustomerWhenOrderIsValidAndReady() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        Order order = anOrder()
                .customerId(customer.id())
                .status(DRAFT)
                .withItems(false)
                .build();
        Product product = aProduct().price(new Money("2500")).build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        orders.add(order);

        loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().toString());

        Customer updatedCustomer = customers.ofId(customer.id()).orElseThrow();
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(10));

    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenCustomerIdDoesNotExist() {
        UUID nonExistingCustomerId = UUID.randomUUID();

        Customer customer = brandNewCustomer().email(new Email("diogomendes@example.com")).build();
        customers.add(customer);

        Order order = anOrder()
                .customerId(customer.id())
                .status(READY)
                .build();
        orders.add(order);

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(nonExistingCustomerId, order.id().toString()));
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenOrderIdDoesNotExist() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        String nonExistingOrderId = TSID.fast().toString();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), nonExistingOrderId));
    }

    @Test
    void shouldThrowCustomerArchivedExceptionWhenCustomerIsArchived() {
        Customer customer = existingCustomer().build();
        customers.add(customer);
        customer.archive();
        customers.add(customer);

        Order order = anOrder()
                .customerId(customer.id())
                .status(READY)
                .build();
        orders.add(order);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().toString()));
    }

    @Test
    void shouldThrowOrderNotBelongsToCustomerExceptionWhenOrderCustomerIdDoesNotMatch() {
        Customer customerA = existingCustomer().id(new CustomerId()).email(new Email("customerA@example.com")).build();
        Customer customerB = existingCustomer().id(new CustomerId()).email(new Email("customerB@example.com")).build();
        customers.add(customerA);
        customers.add(customerB);

        Order order = anOrder()
                .customerId(customerB.id())
                .status(READY)
                .build();
        orders.add(order);

        assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customerA.id().value(), order.id().toString()));

    }

    @Test
    void shouldThrowCantAddLoyaltyPointsOrderIsNotReadyWhenOrderIsNotReady() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        Order order = anOrder()
                .customerId(customer.id())
                .status(PLACED)
                .build();
        orders.add(order);

        assertThatExceptionOfType(CantAddLoyaltyPointsOrderIsNotReadyException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().toString()));
    }

    @Test
    void shouldNotAddLoyaltyPointsWhenOrderAmountIsLessThanThreshold() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        Order order = anOrder()
                .customerId(customer.id())
                .status(DRAFT)
                .withItems(false)
                .build();
        Product product = aProduct().price(new Money("500")).build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        orders.add(order);

        loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().toString());

        Customer updateCustomer = customers.ofId(customer.id()).orElseThrow();
        assertThat(updateCustomer).isNotNull();
        assertThat(updateCustomer.loyaltyPoints()).isEqualTo(ZERO);
    }

}