package com.diogomendes.algashop.ordering.application.shoppingcart.management;

import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import com.diogomendes.algashop.ordering.domain.model.customer.*;
import com.diogomendes.algashop.ordering.domain.model.product.*;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.*;
import com.diogomendes.algashop.ordering.infrastructure.listener.shoppingcart.ShoppingCartEventListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.diogomendes.algashop.ordering.application.shoppingcart.management.ShoppingCartItemInput.builder;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.brandNewCustomer;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProduct;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ShoppingCartManagementApplicationServiceIT {

    @Autowired
    private ShoppingCartManagementApplicationService service;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoSpyBean
    private ShoppingCartEventListener shoppingCartEventListener;

    @Test
    void shouldCreateNewShoppingCartForExistingCustomer() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);

        UUID newShoppingCartId = service.createNew(customer.id().value());

        assertThat(newShoppingCartId).isNotNull();
        Optional<ShoppingCart> createdCart = shoppingCarts.ofId(new ShoppingCartId(newShoppingCartId));
        assertThat(createdCart).isPresent();
        assertThat(createdCart.get().customerId().value()).isEqualTo(customer.id().value());
        assertThat(createdCart.get().isEmpty()).isTrue();

        verify(shoppingCartEventListener).listen(any(ShoppingCartCreatedEvent.class));
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenCreatingNewShoppingCartForNonExistingCustomer() {
        UUID nonExistingCustomerId = UUID.randomUUID();

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.createNew(nonExistingCustomerId));
    }

    @Test
    void shouldThrowCustomerAlreadyHaveShoppingCartExceptionWhenCreatingNewShoppingCartForCustomerWithExistingCart() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> service.createNew(customer.id().value()));
    }

    @Test
    void shouldAddItemToShoppingCartSuccessfully() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        Product product = aProduct().inStock(true).build();
        when(productCatalogService.ofId(product.id())).thenReturn(of(product));

        ShoppingCartItemInput input = builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(product.id().value())
                .quantity(2)
                .build();

        service.addItem(input);

        ShoppingCart updatedCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        assertThat(updatedCart.items()).hasSize(1);
        assertThat(updatedCart.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(updatedCart.items().iterator().next().quantity().value()).isEqualTo(2);

        verify(shoppingCartEventListener).listen(any(ShoppingCartItemAddedEvent.class));
    }

    @Test
    void shouldThrowShoppingCartNotFoundExceptionWhenAddingItemToNonExistingShoppingCart() {
        UUID nonExistingCartId = UUID.randomUUID();
        Product product = aProduct().inStock(true).build();
        when(productCatalogService.ofId(product.id())).thenReturn(of(product));

        ShoppingCartItemInput input = builder()
                .shoppingCartId(nonExistingCartId)
                .productId(product.id().value())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.addItem(input));
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenAddingNonExistingProduct() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        when(productCatalogService.ofId(any())).thenReturn(empty());

        ShoppingCartItemInput input = builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(UUID.randomUUID())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> service.addItem(input));
    }

    @Test
    void shouldThrowProductOutOfStockExceptionWhenAddingOutOfStockProduct() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        Product outOfStockProduct = aProduct().inStock(false).build();
        when(productCatalogService.ofId(outOfStockProduct.id())).thenReturn(of(outOfStockProduct));

        ShoppingCartItemInput input = builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(outOfStockProduct.id().value())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> service.addItem(input));
    }

    @Test
    void shouldRemoveItemFromShoppingCartSuccessfully() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        Product product = aProduct().inStock(true).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);

        ShoppingCartItem itemToRemove = shoppingCart.items().iterator().next();

        service.removeItem(shoppingCart.id().value(), itemToRemove.id().value());

        ShoppingCart updateCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        assertThat(updateCart.items()).isEmpty();

        verify(shoppingCartEventListener).listen(any(ShoppingCartItemRemovedEvent.class));
    }

    @Test
    void shouldThrowShoppingCartNotFoundExceptionWhenRemovingItemFromNonExistingShoppingCart() {
        UUID dummyItemId = UUID.randomUUID();
        UUID nonExistingCartId = UUID.randomUUID();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.removeItem(nonExistingCartId, dummyItemId));
    }

    @Test
    void shouldThrowShoppingCartDoesNotContainItemExceptionWhenRemovingNonExistingItem() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        UUID nonExistingItemId = UUID.randomUUID();

        assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> service.removeItem(shoppingCart.id().value(), nonExistingItemId));
    }

    @Test
    void shouldEmptyShoppingCartSuccessfully() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        Product product = aProduct().inStock(true).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);

        service.empty(shoppingCart.id().value());

        ShoppingCart updateCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        assertThat(updateCart.isEmpty()).isTrue();

        verify(shoppingCartEventListener).listen(any(ShoppingCartEmptiedEvent.class));
    }

    @Test
    void shouldThrowShoppingCartNotFoundExceptionWhenEmptyingNonExistingShoppingCart() {
        UUID nonExistingCartId = UUID.randomUUID();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.empty(nonExistingCartId));
    }

    @Test
    void shouldDeleteShoppingCartSuccessfully() {
        Customer customer = brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        service.delete(shoppingCart.id().value());

        Optional<ShoppingCart> deleteCart = shoppingCarts.ofId(shoppingCart.id());
        assertThat(deleteCart).isNotPresent();
    }

    @Test
    void shouldThrowShoppingCartNotFoundExceptionWhenDeletingNonExistingShoppingCart() {
        UUID nonExistingCartId = UUID.randomUUID();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.delete(nonExistingCartId));
    }

}