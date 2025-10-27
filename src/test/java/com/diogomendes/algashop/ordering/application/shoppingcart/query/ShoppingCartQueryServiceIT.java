package com.diogomendes.algashop.ordering.application.shoppingcart.query;

import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCart.startShopping;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@SpringBootTest
@Transactional
class ShoppingCartQueryServiceIT {

    @Autowired
    private ShoppingCartQueryService queryService;

    @Autowired
    private Customers customers;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Test
    public void shouldFindById() {
        Customer customer = existingCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput output = queryService.findById(shoppingCart.id().value());

        assertWith(output,
                o -> assertThat(o.getId()).isEqualTo(shoppingCart.id().value()),
                o -> assertThat(o.getCustomerId()).isEqualTo(shoppingCart.customerId().value())
        );
    }

    @Test
    public void shouldFindByCustomerId() {
        Customer customer = existingCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput output = queryService.findByCustomerId(customer.id().value());

        assertWith(output,
                o -> assertThat(o.getId()).isEqualTo(shoppingCart.id().value()),
                o -> assertThat(o.getCustomerId()).isEqualTo(shoppingCart.customerId().value())
        );
    }

}