package com.diogomendes.algashop.ordering.application.customer.loyaltypoints;

import com.diogomendes.algashop.ordering.domain.model.customer.*;
import com.diogomendes.algashop.ordering.domain.model.order.Order;
import com.diogomendes.algashop.ordering.domain.model.order.OrderId;
import com.diogomendes.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService {

    private final Orders orders;
    private final Customers customers;
    private final CustomerLoyaltyPointService customerLoyaltyPointService;

    @Transactional
    public void addLoyaltyPoints(UUID rawCustomerId, String rawOrderId) {
        OrderId orderId = new OrderId(rawOrderId);
        CustomerId customerId = new CustomerId(rawCustomerId);

        Order order = orders.ofId(orderId).orElseThrow(OrderNotFoundException::new);
        Customer customer = customers.ofId(customerId).orElseThrow(CustomerNotFoundException::new);

        customerLoyaltyPointService.addPoints(customer, order);

        customers.add(customer);
    }

}
