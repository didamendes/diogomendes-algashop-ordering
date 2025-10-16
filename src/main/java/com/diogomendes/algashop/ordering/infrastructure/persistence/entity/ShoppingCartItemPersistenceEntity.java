package com.diogomendes.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Include;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shopping_cart_items")
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartItemPersistenceEntity {

    @Id
    @Include
    private UUID id;
    private UUID productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Boolean available;
    private BigDecimal totalAmount;

    @JoinColumn
    @ManyToOne(optional = false)
    private ShoppingCartPersistenceEntity shoppingCart;

    @CreatedBy
    private UUID createdByUserId;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lasModifiedByUserId;

    @Version
    private Long version;

    private ShoppingCartPersistenceEntity getShoppingCart() {
        return shoppingCart;
    }

    public UUID getShoppingCardId() {
        return getShoppingCart() == null ? null : getShoppingCart().getId();
    }

}
