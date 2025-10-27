package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.diogomendes.algashop.ordering.domain.model.DomainService;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.diogomendes.algashop.ordering.domain.model.commons.Money.ZERO;
import static com.diogomendes.algashop.ordering.domain.model.order.Order.draft;

@DomainService
@RequiredArgsConstructor
public class CheckoutService {

    private final CustomerHaveFreeShippingSpecification haveFreeShippingSpecification;

    public Order checkout(Customer customer,
                          ShoppingCart shoppingCart,
                          Billing billing,
                          Shipping shipping,
                          PaymentMethod paymentMethod) {
        if (shoppingCart.isEmpty()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        if (shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        Set<ShoppingCartItem> items = shoppingCart.items();

        Order order = draft(shoppingCart.customerId());
        order.changeBilling(billing);

        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }

//        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);

        for (ShoppingCartItem item : items) {
            order.addItem(new Product(item.productId(), item.productName(),
                    item.price(), item.isAvailable()), item.quantity());
        }

        order.place();
        shoppingCart.empty();

        return order;
    }

    private boolean haveFreeShipping(Customer customer) {
        return haveFreeShippingSpecification.isSatisfiedBy(customer);
    }

}
