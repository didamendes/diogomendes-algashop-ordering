package com.diogomendes.algashop.ordering.domain.factory;

import com.diogomendes.algashop.ordering.domain.entity.Order;
import com.diogomendes.algashop.ordering.domain.entity.PaymentMethod;
import com.diogomendes.algashop.ordering.domain.valueobject.Billing;
import com.diogomendes.algashop.ordering.domain.valueobject.Product;
import com.diogomendes.algashop.ordering.domain.valueobject.Quantity;
import com.diogomendes.algashop.ordering.domain.valueobject.Shipping;
import com.diogomendes.algashop.ordering.domain.valueobject.id.CustomerId;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class OrderFactory {

    private OrderFactory() {
    }

    public static Order filled(
            CustomerId customerId,
            Shipping shipping,
            Billing billing,
            PaymentMethod paymentMethod,
            Product product,
            Quantity productQuantity
    ) {
        requireNonNull(customerId);
        requireNonNull(shipping);
        requireNonNull(billing);
        requireNonNull(paymentMethod);
        requireNonNull(product);
        requireNonNull(productQuantity);

        Order order = Order.draft(customerId);

        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, productQuantity);

        return order;
    }

}
