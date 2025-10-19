package com.diogomendes.algashop.ordering.domain.model.customer;

import com.diogomendes.algashop.ordering.domain.model.order.Order;
import com.diogomendes.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.diogomendes.algashop.ordering.domain.model.DomainService;
import com.diogomendes.algashop.ordering.domain.model.commons.Money;

import static com.diogomendes.algashop.ordering.domain.model.customer.LoyaltyPoints.ZERO;
import static java.util.Objects.requireNonNull;

@DomainService
public class CustomerLoyaltyPointService {

    private static final LoyaltyPoints basePoints = new LoyaltyPoints(5);
    private static final Money expectedAmountToGivenPoints = new Money("1000");

    public void addPoints(Customer customer, Order order) {
        requireNonNull(customer);
        requireNonNull(order);

        if (!customer.id().equals(order.customerId())) {
            throw new OrderNotBelongsToCustomerException();
        }

        if (!order.isReady()) {
            throw new CantAddLoyaltyPointsOrderIsNotReadyException();
        }

        customer.addLoyaltyPoints(calculatePoints(order));
    }

    private LoyaltyPoints calculatePoints(Order order) {
        if (shouldGivePointsByAAmount(order.totalAmount())) {
            Money result = order.totalAmount().divide(expectedAmountToGivenPoints);
            return new LoyaltyPoints(result.value().intValue() * basePoints.value());
        }

        return ZERO;
    }

    private boolean shouldGivePointsByAAmount(Money amount) {
        return amount.compareTo(expectedAmountToGivenPoints) >= 0;
    }

}
