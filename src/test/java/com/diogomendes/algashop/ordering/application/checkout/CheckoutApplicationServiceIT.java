package com.diogomendes.algashop.ordering.application.checkout;

import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import com.diogomendes.algashop.ordering.domain.model.order.*;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResult;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.diogomendes.algashop.ordering.application.checkout.CheckoutInputTestDataBuilder.aCheckoutInput;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.order.OrderStatus.PLACED;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProduct;
import static com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder.aShoppingCart;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class CheckoutApplicationServiceIT {

    @Autowired
    private CheckoutApplicationService service;

    @Autowired
    private Orders orders;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private OriginAddressService originAddressService;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @BeforeEach
    public void setup() {
        when(shippingCostService.calculate(any(CalculationRequest.class)))
                .thenReturn(new CalculationResult(
                        new Money("10.00"),
                        LocalDate.now().plusDays(3)
                ));
        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(existingCustomer().build());
        }
    }

    @Test
    void shouldCheckout() {
        Product product = aProduct().inStock(true).build();

        ShoppingCart shoppingCart = aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);

        CheckoutInput input = aCheckoutInput()
                .shoppingCartId(shoppingCart.id().value())
                .build();

        String orderId = service.checkout(input);

        assertThat(orderId).isNotBlank();
        assertThat(orders.exists(new OrderId(orderId))).isTrue();

        Optional<Order> createdOrder = orders.ofId(new OrderId(orderId));
        assertThat(createdOrder).isPresent();
        assertThat(createdOrder.get().status()).isEqualTo(PLACED);
        assertThat(createdOrder.get().totalAmount().value()).isGreaterThan(ZERO);

        Optional<ShoppingCart> updatedCart = shoppingCarts.ofId(shoppingCart.id());
        assertThat(updatedCart).isPresent();
        assertThat(updatedCart.get().isEmpty()).isTrue();
    }

    @Test
    void shouldThrowShoppingCartNotFoundExceptionWhenCheckoutWithNonExistingShoppingCart() {
        CheckoutInput input = aCheckoutInput()
                .shoppingCartId(UUID.randomUUID())
                .build();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.checkout(input));
    }

    @Test
    void shouldThrowShoppingCartCantProceedToCheckoutExceptionWhenCartIsEmpty() {
        ShoppingCart shoppingCart = aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        CheckoutInput input = aCheckoutInput()
                .shoppingCartId(shoppingCart.id().value())
                .build();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(input));
    }

    @Test
    void shouldThrowShoppingCartCantProceedToCheckoutExceptionWhenCartContainsUnavailableItems() {
        Product product = aProduct().inStock(true).build();
        Product unavailableProduct = aProduct().id(product.id()).inStock(false).build();

        ShoppingCart shoppingCart = aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.refreshItem(unavailableProduct);
        shoppingCarts.add(shoppingCart);

        CheckoutInput input = aCheckoutInput()
                .shoppingCartId(shoppingCart.id().value())
                .build();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(input));
    }

}