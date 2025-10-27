package com.diogomendes.algashop.ordering.application.order.query;

import com.diogomendes.algashop.ordering.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort.Direction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.diogomendes.algashop.ordering.application.order.query.OrderFilter.SortType.PLACED_AT;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderFilter extends SortablePageFilter<OrderFilter.SortType> {

    private String status;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime placedAtFrom;
    private OffsetDateTime placedAtTo;
    private BigDecimal totalAmountFrom;
    private BigDecimal totalAmountTo;

    public OrderFilter(int size, int page) {
        super(size, page);
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? PLACED_AT : getSortByProperty();
    }

    @Override
    public Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        PLACED_AT("placedAt"),
        PAID_AT("paidAt"),
        CANCELED_AT("canceledAt"),
        READY_AT("readyAt"),
        STATUS("status"),
        PAYMENT_METHOD("paymentMethod"),;

        private final String propertyName;
    }
}
