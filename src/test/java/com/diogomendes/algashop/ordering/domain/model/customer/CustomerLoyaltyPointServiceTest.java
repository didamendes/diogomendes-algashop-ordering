package com.diogomendes.algashop.ordering.domain.model.customer;

import com.diogomendes.algashop.ordering.domain.model.order.Order;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import org.junit.jupiter.api.Test;

import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderStatus.DRAFT;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderStatus.READY;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.anOrder;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProductAltRamMemory;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerLoyaltyPointServiceTest {

    CustomerLoyaltyPointService customerLoyaltyPointService = new CustomerLoyaltyPointService();

    @Test
    public void givenValidCustomerAndOrder_WhenAddingPoints_ShouldAccumulate() {
        Customer customer = existingCustomer().build();

        Order order = anOrder().status(READY).build();

        customerLoyaltyPointService.addPoints(customer, order);

        assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    public void givenValidCustomerAndOrderWithLowTotalAmount_WhenAddingPoints_ShouldNotAccumulate() {
        Customer customer = existingCustomer().build();
        Product product = aProductAltRamMemory().build();

        Order order = anOrder().withItems(false).status(DRAFT).build();
        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        customerLoyaltyPointService.addPoints(customer, order);

        assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(0));
    }

}