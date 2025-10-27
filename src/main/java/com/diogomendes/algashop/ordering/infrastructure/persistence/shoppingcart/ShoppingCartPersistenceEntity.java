package com.diogomendes.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.diogomendes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Include;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter
@Setter
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "\"shopping_carts\"")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartPersistenceEntity
    extends AbstractAggregateRoot<ShoppingCartPersistenceEntity> {

    @Id
    @Include
    private UUID id;
    private BigDecimal totalAmount;
    private Integer totalItems;

    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;

    @OneToMany(mappedBy = "shoppingCart", cascade = ALL)
    private Set<ShoppingCartItemPersistenceEntity> items = new HashSet<>();

    @CreatedBy
    private UUID createdByUserId;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

    @Builder(toBuilder = true)
    public ShoppingCartPersistenceEntity(UUID id, BigDecimal totalAmount, Integer totalItems, OffsetDateTime createdAt,
                                         CustomerPersistenceEntity customer, Set<ShoppingCartItemPersistenceEntity> items) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.customer = customer;
        this.createdAt = createdAt;
        this.replaceItems(items);
    }

    public void addItem(Set<ShoppingCartItemPersistenceEntity> items)  {
        for (ShoppingCartItemPersistenceEntity item : items) {
            this.addItem(item);
        }
    }

    public void addItem(ShoppingCartItemPersistenceEntity item) {
        if (item == null) {
            return;
        }

        if (this.getItems() == null) {
            this.setItems(new HashSet<>());
        }

        item.setShoppingCart(this);
        this.items.add(item);
    }

    public UUID getCustomerId() {
        return customer == null ? null : customer.getId();
    }

    public void replaceItems(Set<ShoppingCartItemPersistenceEntity> updatedItems) {
        if (updatedItems == null || updatedItems.isEmpty()) {
            this.setItems(new HashSet<>());
            return;
        }

        updatedItems.forEach(i -> i.setShoppingCart(this));
        this.setItems(updatedItems);
    }

    public Collection<Object> getEvents() {
        return super.domainEvents();
    }

    public void addEvents(Collection<Object> events) {
        if (events != null) {
            for (Object event : events) {
                this.registerEvent(event);
            }
        }
    }

}
