package com.diogomendes.algashop.ordering.application.checkout;

import com.diogomendes.algashop.ordering.application.commons.AddressData;
import com.diogomendes.algashop.ordering.application.order.query.BillingData;
import com.diogomendes.algashop.ordering.domain.model.commons.*;
import com.diogomendes.algashop.ordering.domain.model.order.Billing;
import org.springframework.stereotype.Component;

@Component
public class BillingInputDisassembler {

    public Billing toDomainModel(BillingData billingData) {
        AddressData address = billingData.getAddress();
        return Billing.builder()
                .fullName(new FullName(billingData.getFirstName(), billingData.getLastName()))
                .document(new Document(billingData.getDocument()))
                .phone(new Phone(billingData.getPhone()))
                .email(new Email(billingData.getEmail()))
                .address(Address.builder()
                        .street(address.getStreet())
                        .number(address.getNumber())
                        .complement(address.getComplement())
                        .neighborhood(address.getNeighborhood())
                        .city(address.getCity())
                        .state(address.getState())
                        .zipCode(new ZipCode(address.getZipCode()))
                        .build())
                .build();
    }

}
