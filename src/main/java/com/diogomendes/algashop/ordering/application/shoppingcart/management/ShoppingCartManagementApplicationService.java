package com.diogomendes.algashop.ordering.application.shoppingcart.management;

import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.product.ProductCatalogService;
import com.diogomendes.algashop.ordering.domain.model.product.ProductId;
import com.diogomendes.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class ShoppingCartManagementApplicationService {

    private final ShoppingCarts shoppingCarts;
    private final ShoppingService shoppingService;
    private final ProductCatalogService productCatalogService;

    @Transactional
    public void addItem(ShoppingCartItemInput input) {
        requireNonNull(input);
        ProductId productId = new ProductId(input.getProductId());
        ShoppingCartId shoppingCartId = new ShoppingCartId(input.getShoppingCartId());

        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(ShoppingCartNotFoundException::new);

        Product product = productCatalogService.ofId(productId)
                .orElseThrow(ProductNotFoundException::new);

        shoppingCart.addItem(product, new Quantity(input.getQuantity()));

        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public UUID createNew(UUID rawCustomerId) {
        requireNonNull(rawCustomerId);
        CustomerId customerId = new CustomerId(rawCustomerId);
        ShoppingCart shoppingCart = shoppingService.startShopping(customerId);
        shoppingCarts.add(shoppingCart);
        return shoppingCart.id().value();
    }

    @Transactional
    public void removeItem(UUID rawShoppingCartId, UUID rawShoppingCartItemId) {
        requireNonNull(rawShoppingCartId);
        requireNonNull(rawShoppingCartItemId);
        ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId(rawShoppingCartItemId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCart.removeItem(shoppingCartItemId);
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public void empty(UUID rawShoppingCartId) {
        requireNonNull(rawShoppingCartId);
        ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCart.empty();
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public void delete(UUID rawShoppingCartId) {
        requireNonNull(rawShoppingCartId);
        ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCarts.remove(shoppingCart);
    }

}
