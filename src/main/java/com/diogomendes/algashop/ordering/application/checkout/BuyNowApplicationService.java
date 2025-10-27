package com.diogomendes.algashop.ordering.application.checkout;

import com.diogomendes.algashop.ordering.domain.model.commons.Quantity;
import com.diogomendes.algashop.ordering.domain.model.commons.ZipCode;
import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import com.diogomendes.algashop.ordering.domain.model.order.*;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResult;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.product.ProductCatalogService;
import com.diogomendes.algashop.ordering.domain.model.product.ProductId;
import com.diogomendes.algashop.ordering.domain.model.product.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService {

    private final BuyNowService buyNowService;
    private final ProductCatalogService productCatalogService;

    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;

    private final Orders orders;
    private final Customers customers;

    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler billingInputDisassembler;

    @Transactional
    public String buyNow(BuyNowInput input) {
        requireNonNull(input);

        Quantity quantity = new Quantity(input.getQuantity());
        CustomerId customerId = new CustomerId(input.getCustomerId());
        PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());
        Customer customer = customers.ofId(customerId).orElseThrow(CustomerNotFoundException::new);

        Product product = findProduct(new ProductId(input.getProductId()));
        var shippingCalculationResult = calculateShippingCost(input.getShipping());

        Billing billing = billingInputDisassembler.toDomainModel(input.getBilling());
        Shipping shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), shippingCalculationResult);

        Order order = buyNowService.buyNow(
                product,
                customer,
                billing,
                shipping,
                quantity,
                paymentMethod
        );

        orders.add(order);

        return order.id().toString();
    }

    private CalculationResult calculateShippingCost(ShippingInput shipping) {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode(shipping.getAddress().getZipCode());

       return shippingCostService.calculate(new CalculationRequest(
                origin,
                destination
        ));
    }

    private Product findProduct(ProductId productId) {
        return productCatalogService.ofId(productId)
                .orElseThrow(ProductNotFoundException::new);
    }

}
