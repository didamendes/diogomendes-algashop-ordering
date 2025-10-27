package com.diogomendes.algashop.ordering.application.customer.query;

import com.diogomendes.algashop.ordering.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import static com.diogomendes.algashop.ordering.application.customer.query.CustomerFilter.SortType.REGISTERED_AT;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter extends SortablePageFilter<CustomerFilter.SortType> {

    private String email;
    private String firstName;

    public CustomerFilter(int size, int page) {
        super(size, page);
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? REGISTERED_AT : getSortByProperty();
    }

    @Override
    public Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        REGISTERED_AT("registeredAt"),
        FIRST_NAME("firstName");

        private final String propertyName;
    }

}
