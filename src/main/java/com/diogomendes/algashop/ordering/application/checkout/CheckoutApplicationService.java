package com.diogomendes.algashop.ordering.application.checkout;

import com.diogomendes.algashop.ordering.domain.model.commons.ZipCode;
import com.diogomendes.algashop.ordering.domain.model.order.CheckoutService;
import com.diogomendes.algashop.ordering.domain.model.order.Order;
import com.diogomendes.algashop.ordering.domain.model.order.Orders;
import com.diogomendes.algashop.ordering.domain.model.order.PaymentMethod;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.diogomendes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResult;
import com.diogomendes.algashop.ordering.domain.model.product.Product;
import com.diogomendes.algashop.ordering.domain.model.product.ProductCatalogService;
import com.diogomendes.algashop.ordering.domain.model.product.ProductId;
import com.diogomendes.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.diogomendes.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.ordering.domain.model.order.PaymentMethod.valueOf;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {

    private final Orders orders;
    private final ShoppingCarts shoppingCarts;
    private final CheckoutService checkoutService;

    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;

    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final ProductCatalogService productCatalogService;

    @Transactional
    public String checkout(CheckoutInput input) {
        requireNonNull(input);
        PaymentMethod paymentMethod = valueOf(input.getPaymentMethod());

        ShoppingCartId shoppingCartId = new ShoppingCartId(input.getShoppingCartId());
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId).orElseThrow(ShoppingCartNotFoundException::new);

        CalculationResult calculationResult = calculateShippingCost(input.getShipping());

        Order order = checkoutService.checkout(shoppingCart,
                billingInputDisassembler.toDomainModel(input.getBilling()),
                shippingInputDisassembler.toDomainModel(input.getShipping(), calculationResult),
                paymentMethod);

        orders.add(order);
        shoppingCarts.add(shoppingCart);

        return order.id().toString();
    }

    private CalculationResult calculateShippingCost(ShippingInput shipping) {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode(shipping.getAddress().getZipCode());
        return shippingCostService.calculate(new CalculationRequest(origin, destination));
    }

    private Product findProduct(ProductId productId) {
        return productCatalogService.ofId(productId).orElseThrow(ProductNotFoundException::new);
    }

}
