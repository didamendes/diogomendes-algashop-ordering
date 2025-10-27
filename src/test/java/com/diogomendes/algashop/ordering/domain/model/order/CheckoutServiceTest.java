package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.diogomendes.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.diogomendes.algashop.ordering.domain.model.commons.Money.ZERO;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.aBilling;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderTestDataBuilder.aShipping;
import static com.diogomendes.algashop.ordering.domain.model.order.PaymentMethod.CREDIT_CARD;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProduct;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProductAltRamMemory;
import static com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCart.startShopping;
import static com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    private CheckoutService checkoutService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setup() {
        CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(
                orders,
                new LoyaltyPoints(100),
                2L,
                new LoyaltyPoints(2000)
        );
        checkoutService = new CheckoutService(specification);
    }

    @Test
    void givenValidShoppingCart_whenCheckout_shouldReturnPlacedOrderAndEmptyShoppingCart() {
        Customer customer = existingCustomer().build();

        ShoppingCart shoppingCart = startShopping(customer.id());
        shoppingCart.addItem(aProduct().build(), new Quantity(2));
        shoppingCart.addItem(aProductAltRamMemory().build(), new Quantity(1));

        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        Money shoppingCartToTotalAmount = shoppingCart.totalAmount();
        Quantity expectedOrderTotalItems = shoppingCart.totalItems();
        int expectedOrderItemsCount = shoppingCart.items().size();

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

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
        assertThat(shoppingCart.totalAmount()).isEqualTo(ZERO);
        assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
    }

    @Test
    void givenShoppingCartWithUnavailableItems_whenCheckout_shouldThrowShoppingCartCantProceedToCheckoutException() {
        Customer customer = existingCustomer().build();

        ShoppingCart shoppingCart = aShoppingCart().customerId(customer.id()).withItems(false).build();
        Product product = aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));

        Product productUnivailable = aProduct().inStock(false).build();
        shoppingCart.refreshItem(productUnivailable);

        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isFalse();
        assertThat(shoppingCart.items()).hasSize(1);
    }

    @Test
    void givenEmptyShoppingCart_whenCheckout_shouldThrowShoppingCartCantProceedToCheckoutException() {
        Customer customer = existingCustomer().build();

        ShoppingCart shoppingCart = aShoppingCart().customerId(customer.id()).withItems(false).build();
        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isTrue();
    }
    
    @Test
    void givenShoppingCartWithUnavailableItems_whenCheckout_shouldNotModifyShoppingCartState() {
        Customer customer = existingCustomer().build();

        ShoppingCart shoppingCart = startShopping(customer.id());
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
                .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isFalse();

        Money expectedTotalAmount = productInStock.price()
                .multiply(new Quantity(2)).add(productAlt.price());
        assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
        assertThat(shoppingCart.items()).hasSize(2);
    }

    @Test
    void givenValidShoppingCartAndCustomerWithFreeShipping_whenCheckout_shouldReturnPlacedOrderWithFreeShipping() {
        Customer customer = existingCustomer().loyaltyPoints(new LoyaltyPoints(3000)).build();

        ShoppingCart shoppingCart = startShopping(customer.id());
        shoppingCart.addItem(aProduct().build(), new Quantity(2));
        shoppingCart.addItem(aProductAltRamMemory().build(), new Quantity(1));

        Billing billing = aBilling();
        Shipping shipping = aShipping();
        PaymentMethod paymentMethod = CREDIT_CARD;

        Money shoppingCartToTotalAmount = shoppingCart.totalAmount();
        Quantity expectedOrderTotalItems = shoppingCart.totalItems();
        int expectedOrderItemsCount = shoppingCart.items().size();

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(shoppingCart.customerId());
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.billing()).isEqualTo(billing);
        assertThat(order.shipping()).isEqualTo(shipping.toBuilder().cost(ZERO).build());
        assertThat(order.isPlaced()).isTrue();

        assertThat(order.totalAmount()).isEqualTo(shoppingCartToTotalAmount);
        assertThat(order.totalItems()).isEqualTo(expectedOrderTotalItems);
        assertThat(order.items()).hasSize(expectedOrderItemsCount);

        assertThat(shoppingCart.isEmpty()).isTrue();
        assertThat(shoppingCart.totalAmount()).isEqualTo(ZERO);
        assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
    }

}