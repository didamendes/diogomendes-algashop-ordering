package com.diogomendes.algashop.ordering.infrastructure.persistence.provider;

import com.diogomendes.algashop.ordering.domain.model.entity.*;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Money;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Product;
import com.diogomendes.algashop.ordering.domain.model.valueobject.Quantity;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.diogomendes.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassembler;
import com.diogomendes.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.diogomendes.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.entity.ProductTestDataBuilder.aProduct;
import static com.diogomendes.algashop.ordering.domain.model.entity.ProductTestDataBuilder.aProductAltRamMemory;
import static com.diogomendes.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.transaction.annotation.Propagation.NEVER;

@DataJpaTest
@Import({
        ShoppingCartUpdateProvider.class,
        ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShoppingCartUpdateProviderIT {

    private ShoppingCartsPersistenceProvider persistenceProvider;
    private CustomersPersistenceProvider customersPersistenceProvider;
    private ShoppingCartPersistenceEntityRepository entityRepository;

    private ShoppingCartUpdateProvider shoppingCartUpdateProvider;

    @Autowired
    public ShoppingCartUpdateProviderIT(ShoppingCartsPersistenceProvider persistenceProvider,
                                        CustomersPersistenceProvider customersPersistenceProvider,
                                        ShoppingCartPersistenceEntityRepository entityRepository,
                                        ShoppingCartUpdateProvider shoppingCartUpdateProvider) {
        this.persistenceProvider = persistenceProvider;
        this.customersPersistenceProvider = customersPersistenceProvider;
        this.entityRepository = entityRepository;
        this.shoppingCartUpdateProvider = shoppingCartUpdateProvider;
    }

    @BeforeEach
    public void setup() {
        if (!customersPersistenceProvider.exists(DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(existingCustomer().build());
        }
    }

    @Test
    @Transactional(propagation = NEVER)
    public void shouldUpdateItemPriceAndTotalAmount() {
        ShoppingCart shoppingCart = aShoppingCart().withItems(false).build();

        Product product1 = aProduct().price(new Money("2000")).build();
        Product product2 = aProductAltRamMemory().price(new Money("200")).build();
        
        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));
        
        persistenceProvider.add(shoppingCart);

        ProductId productIdToUpdate = product1.id();
        Money newProduct1Price = new Money("1500");
        Money expectedNewItemTotalPrice = newProduct1Price.multiply(new Quantity(2));
        Money expectedNewCartTotalAmount = expectedNewItemTotalPrice.add(new Money("200"));

        shoppingCartUpdateProvider.adjustPrice(productIdToUpdate, newProduct1Price);

        ShoppingCart updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        assertThat(updatedShoppingCart.totalAmount()).isEqualTo(expectedNewCartTotalAmount);
        assertThat(updatedShoppingCart.totalItems()).isEqualTo(new Quantity(3));

        ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

        assertThat(item.totalAmount()).isEqualTo(expectedNewItemTotalPrice);
        assertThat(item.price()).isEqualTo(newProduct1Price);
    }

    @Test
    @Transactional(propagation = NEVER)
    void shouldUpdateItemAvailability() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        Product product1 = ProductTestDataBuilder.aProduct()
                .price(new Money("2000"))
                .inStock(true).build();
        Product product2 = ProductTestDataBuilder.aProductAltRamMemory()
                .price(new Money("200"))
                .inStock(true).build();

        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));

        persistenceProvider.add(shoppingCart);

        var productIdToUpdate = product1.id();
        var productIdNotToUpdate = product2.id();

        shoppingCartUpdateProvider.changeAvailability(productIdToUpdate, false);

        ShoppingCart updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

        Assertions.assertThat(item.isAvailable()).isFalse();

        ShoppingCartItem item2 = updatedShoppingCart.findItem(productIdNotToUpdate);

        Assertions.assertThat(item2.isAvailable()).isTrue();

    }

}