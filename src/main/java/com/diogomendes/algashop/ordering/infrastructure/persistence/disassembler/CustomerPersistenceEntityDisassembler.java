package com.diogomendes.algashop.ordering.infrastructure.persistence.disassembler;

import com.diogomendes.algashop.ordering.domain.model.entity.Customer;
import com.diogomendes.algashop.ordering.domain.model.valueobject.*;
import com.diogomendes.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.diogomendes.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.diogomendes.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityDisassembler {

    public Customer toDomainEntity(CustomerPersistenceEntity entity) {
        return Customer.existing()
                .id(new CustomerId(entity.getId()))
                .fullName(new FullName(entity.getFirstName(), entity.getLastName()))
                .birthDate(entity.getBirthDate() != null ? new BirthDate(entity.getBirthDate()) : null)
                .email(new Email(entity.getEmail()))
                .phone(new Phone(entity.getPhone()))
                .document(new Document(entity.getDocument()))
                .loyaltyPoints(new LoyaltyPoints(entity.getLoyaltyPoints()))
                .promotionNotificationsAllowed(entity.getPromotionNotificationsAllowed())
                .archived(entity.getArchived())
                .registeredAt(entity.getRegisteredAt())
                .archivedAt(entity.getArchivedAt())
                .address(toAddressValueObject(entity.getAddress()))
                .version(entity.getVersion())
                .build();
    }

    private Address toAddressValueObject(AddressEmbeddable addressEmbeddable) {
        return Address.builder()
                .city(addressEmbeddable.getCity())
                .state(addressEmbeddable.getState())
                .street(addressEmbeddable.getStreet())
                .number(addressEmbeddable.getNumber())
                .complement(addressEmbeddable.getComplement())
                .neighborhood(addressEmbeddable.getNeighborhood())
                .zipCode(new ZipCode(addressEmbeddable.getZipCode()))
                .build();
    }

}
