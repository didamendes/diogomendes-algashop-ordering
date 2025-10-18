package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.*;
import com.diogomendes.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import org.junit.jupiter.api.Test;

import static com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder.aBilling;
import static com.diogomendes.algashop.ordering.domain.model.entity.OrderTestDataBuilder.aShipping;
import static com.diogomendes.algashop.ordering.domain.model.entity.PaymentMethod.CREDIT_CARD;
import static com.diogomendes.algashop.ordering.domain.model.entity.ProductTestDataBuilder.aProduct;
import static com.diogomendes.algashop.ordering.domain.model.entity.ProductTestDataBuilder.aProductAltRamMemory;
import static com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCart.startShopping;
import static com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class CheckoutServiceTest {

    private final CheckoutService checkoutService = new CheckoutService();

    @Test
    void givenValidShoppingCart_whenCheckout_shouldReturnPlacedPlacedOrderAndEmptyShoppingCart() {
        ShoppingCart shoppingCart = startShopping(aShoppingCart().customerId);
        shoppingCart.addItem(aProduct().build(), new Quantity(2));
        shoppingCart.addItem(aProductAltRamMemory().build(), new Quantity(1));

        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        Money shoppingCartToTotalAmount = shoppingCart.totalAmount();
        Quantity expectedOrderTotalItems = shoppingCart.totalItems();
        int expectedOrderItemsCount = shoppingCart.items().size();

        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(shoppingCart.customerId());
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.billing()).isEqualTo(billing);
        assertThat(order.shipping()).isEqualTo(shipping);
        assertThat(order.isPlaced()).isTrue();

        Money expectedTotalAmountWithShipping = shoppingCartToTotalAmount.add(shipping.cost());
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmountWithShipping);
        assertThat(order.totalItems()).isEqualTo(expectedOrderTotalItems);
        assertThat(order.items()).hasSize(expectedOrderItemsCount);

        assertThat(shoppingCart.isEmpty()).isTrue();
        assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
        assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
    }

    @Test
    void givenShoppingCartWithUnavailableItems_whenCheckout_shouldThrowShoppingCartCantProceedToCheckoutException() {
        ShoppingCart shoppingCart = aShoppingCart().withItems(false).build();
        Product product = aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));

        Product productUnivailable = aProduct().inStock(false).build();
        shoppingCart.refreshItem(productUnivailable);

        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isFalse();
        assertThat(shoppingCart.items()).hasSize(1);
    }

    @Test
    void givenEmptyShoppingCart_whenCheckout_shouldThrowShoppingCartCantProceedToCheckoutException() {
        ShoppingCart shoppingCart = aShoppingCart().withItems(false).build();
        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isTrue();
    }
    
    @Test
    void givenShoppingCartWithUnavailableItems_whenCheckout_shouldNotModifyShoppingCartState() {
        ShoppingCart shoppingCart = startShopping(aShoppingCart().customerId);
        Product productInStock = aProduct().build();
        shoppingCart.addItem(productInStock, new Quantity(2));

        Money initialTotalAmount = shoppingCart.totalAmount();
        Quantity initialTotalItems = shoppingCart.totalItems();

        Product productAlt = aProductAltRamMemory().build();
        shoppingCart.addItem(productAlt, new Quantity(1));

        Product productAAltUnavailable = aProductAltRamMemory().id(productAlt.id()).inStock(false).build();
        shoppingCart.refreshItem(productAAltUnavailable);

        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isFalse();

        Money expectedTotalAmount = productInStock.price()
                .multiply(new Quantity(2)).add(productAlt.price());
        assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
        assertThat(shoppingCart.items()).hasSize(2);
    }

}