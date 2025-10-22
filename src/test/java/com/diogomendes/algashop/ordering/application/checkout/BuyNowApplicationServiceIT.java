package com.diogomendes.algashop.ordering.application.checkout;

import com.diogomendes.algashop.ordering.domain.model.commons.Money;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import com.diogomendes.algashop.ordering.domain.model.order.OrderId;
import com.diogomendes.algashop.ordering.domain.model.order.Orders;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResult;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.product.ProductCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.ordering.application.checkout.BuyNowInputTestDataBuilder.aBuyNowInput;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static com.diogomendes.algashop.ordering.domain.model.product.ProductTestDataBuilder.aProduct;
import static java.time.LocalDate.now;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class BuyNowApplicationServiceIT {

    @Autowired
    private BuyNowApplicationService buyNowApplicationService;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @BeforeEach
    public void setup() {
        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(existingCustomer().build());
        }
    }

    @Test
    public void shouldBuyNow() {
        Product product = aProduct().build();
        when(productCatalogService.ofId(product.id())).thenReturn(of(product));

        when(shippingCostService.calculate(any(CalculationRequest.class)))
                .thenReturn(new CalculationResult(
                        new Money("10.00"),
                        now().plusDays(3)
                ));

        BuyNowInput input = aBuyNowInput().build();

        String orderId = buyNowApplicationService.buyNow(input);

        assertThat(orderId).isNotBlank();
        assertThat(orders.exists(new OrderId(orderId))).isTrue();
    }

}