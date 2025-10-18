package com.diogomendes.algashop.ordering.domain.model.service;

import com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.diogomendes.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.repository.Customers;
import com.diogomendes.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Money;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Quantity;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder.aShoppingCart;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

    @InjectMocks
    private ShoppingService shoppingService;

    @Mock
    private ShoppingCarts shoppingCarts;

    @Mock
    private Customers customers;

    @Test
    void givenExistingCustomerAndNoShoppingCart_whenStartShopping_shouldReturnNewShoppingCart() {
        CustomerId customerId = DEFAULT_CUSTOMER_ID;

        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(empty());

        ShoppingCart newShoppingCart = shoppingService.startShopping(customerId);

        assertThat(newShoppingCart).isNotNull();
        assertThat(newShoppingCart.customerId()).isEqualTo(customerId);
        assertThat(newShoppingCart.isEmpty()).isTrue();
        assertThat(newShoppingCart.totalAmount()).isEqualTo(Money.ZERO);
        assertThat(newShoppingCart.totalItems()).isEqualTo(Quantity.ZERO);

        verify(customers).exists(customerId);
        verify(shoppingCarts).ofCustomer(customerId);
    }

    @Test
    void givenNonExistingCustomer_whenStartShopping_shouldThrowCustomerNotFoundException() {
        CustomerId customerId = new CustomerId();

        when(customers.exists(customerId)).thenReturn(false);

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId));

        verify(customers).exists(customerId);
        verify(shoppingCarts, never()).ofCustomer(any());
    }

    @Test
    void givenExistingCustomerAndExistingShoppingCart_whenStartShopping_shouldThrowCustomerAlreadyHaveShoppingCartException() {
        CustomerId customerId = DEFAULT_CUSTOMER_ID;
        ShoppingCart shoppingCart = aShoppingCart().customerId(customerId).build();

        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(of(shoppingCart));

        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId));

        verify(customers).exists(customerId);
        verify(shoppingCarts).ofCustomer(customerId);
    }

}