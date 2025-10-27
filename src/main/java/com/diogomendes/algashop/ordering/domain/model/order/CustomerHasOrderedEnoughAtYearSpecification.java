package com.diogomendes.algashop.ordering.domain.model.order;

import com.diogomendes.algashop.ordering.domain.model.Specification;
import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerHasOrderedEnoughAtYearSpecification implements Specification<Customer> {

    private final Orders orders;
    private final long expectedOrdersCount;

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return orders.salesQuantityByCustomerInYear(
                customer.id(),
                java.time.Year.now()
        ) >= expectedOrdersCount;
    }
}
