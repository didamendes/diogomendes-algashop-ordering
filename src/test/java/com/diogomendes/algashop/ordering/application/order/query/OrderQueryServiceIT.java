package com.diogomendes.algashop.ordering.application.order.query;

import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import com.diogomendes.algashop.ordering.domain.model.order.Order;
import com.diogomendes.algashop.ordering.domain.model.order.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.ordering.application.order.query.OrderFilter.SortType.STATUS;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderStatus.*;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.anOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.ASC;

@SpringBootTest
@Transactional
class OrderQueryServiceIT {

    @Autowired
    private OrderQueryService queryService;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @Test
    public void shouldFindById() {
        Customer customer = existingCustomer().build();
        customers.add(customer);

        Order order = anOrder().customerId(customer.id()).build();
        orders.add(order);

        OrderDetailOutput output = queryService.findById(order.id().toString());

        assertThat(output)
                .extracting(
                        OrderDetailOutput::getId,
                        OrderDetailOutput::getTotalAmount
                ).containsExactly(
                        order.id().toString(),
                order.totalAmount().value()
                );
    }

    @Test
    public void shouldFilterByPage() {
        Customer customer = existingCustomer().build();
        customers.add(customer);

        orders.add(anOrder().status(DRAFT).withItems(false).customerId(customer.id()).build());
        orders.add(anOrder().status(PLACED).customerId(customer.id()).build());
        orders.add(anOrder().status(PAID).customerId(customer.id()).build());
        orders.add(anOrder().status(READY).customerId(customer.id()).build());
        orders.add(anOrder().status(CANCELED).customerId(customer.id()).build());

        Page<OrderSummaryOutput> page = queryService.filter(new OrderFilter(3, 0));

        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void shouldFilterByCustomerId() {
        Customer customer1 = existingCustomer().build();
        customers.add(customer1);

        orders.add(anOrder().status(DRAFT).withItems(false).customerId(customer1.id()).build());
        orders.add(anOrder().status(PLACED).customerId(customer1.id()).build());

        Customer customer2 = existingCustomer().id(new CustomerId()).build();
        customers.add(customer2);
        orders.add(anOrder().status(PAID).customerId(customer2.id()).build());
        orders.add(anOrder().status(READY).customerId(customer2.id()).build());
        orders.add(anOrder().status(CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = new OrderFilter();
        filter.setCustomerId(customer1.id().value());

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shoudFilterByMultipleParams() {
        Customer customer1 = existingCustomer().build();
        customers.add(customer1);

        orders.add(anOrder().status(DRAFT).withItems(false).customerId(customer1.id()).build());
        Order order1 = anOrder().status(PLACED).customerId(customer1.id()).build();
        orders.add(order1);

        Customer customer2 = existingCustomer().id(new CustomerId()).build();
        customers.add(customer2);
        orders.add(anOrder().status(PAID).customerId(customer2.id()).build());
        orders.add(anOrder().status(READY).customerId(customer2.id()).build());
        orders.add(anOrder().status(CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = new OrderFilter();
        filter.setCustomerId(customer1.id().value());
        filter.setStatus(PLACED.toString().toLowerCase());
        filter.setTotalAmountFrom(order1.totalAmount().value());

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void givenInvalidOrderId_whenFilter_shoudReturnEmptyPage() {
        Customer customer1 = existingCustomer().build();
        customers.add(customer1);

        orders.add(anOrder().status(DRAFT).withItems(false).customerId(customer1.id()).build());
        Order order1 = anOrder().status(PLACED).customerId(customer1.id()).build();
        orders.add(order1);

        Customer customer2 = existingCustomer().id(new CustomerId()).build();
        customers.add(customer2);
        orders.add(anOrder().status(PAID).customerId(customer2.id()).build());
        orders.add(anOrder().status(READY).customerId(customer2.id()).build());
        orders.add(anOrder().status(CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = new OrderFilter();
        filter.setOrderId("invalid-order-id");

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void shoudOrderByStatus() {
        Customer customer1 = existingCustomer().build();
        customers.add(customer1);

        orders.add(anOrder().status(DRAFT).withItems(false).customerId(customer1.id()).build());
        orders.add(anOrder().status(PLACED).customerId(customer1.id()).build());

        Customer customer2 = existingCustomer().id(new CustomerId()).build();
        customers.add(customer2);
        orders.add(anOrder().status(PAID).customerId(customer2.id()).build());
        orders.add(anOrder().status(READY).customerId(customer2.id()).build());
        orders.add(anOrder().status(CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = new OrderFilter();
        filter.setSortByProperty(STATUS);
        filter.setSortDirection(ASC);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getContent().getFirst().getStatus()).isEqualTo(CANCELED.toString());
    }

}