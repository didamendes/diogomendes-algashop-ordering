package com.diogomendes.algashop.ordering.application.checkout;

import com.diogomendes.algashop.ordering.application.commons.AddressData;
import com.diogomendes.algashop.ordering.application.order.query.RecipientData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingInput {
    private RecipientData recipient;
    private AddressData address;
}
