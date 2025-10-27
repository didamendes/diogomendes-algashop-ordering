package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.DomainService;
import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import lombok.RequiredArgsConstructor;

import java.time.Year;

import static com.diogomendes.algashop.ordering.domain.model.commons.Money.ZERO;
import static com.diogomendes.algashop.ordering.domain.model.order.Order.draft;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {

    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;

    public Order buyNow(Product product,
                        Customer customer,
                        Billing billing,
                        Shipping shipping,
                        Quantity quantity,
                        PaymentMethod paymentMethod) {
        product.checkOutOfStock();

        Order order = draft(customer.id());
        order.changeBilling(billing);

        order.changePaymentMethod(paymentMethod);
        order.addItem(product, quantity);

        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }

        order.place();

        return order;
    }

    private boolean haveFreeShipping(Customer customer) {
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }

}
