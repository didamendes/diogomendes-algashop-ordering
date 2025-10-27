package com.diogomendes.algashop.ordering.application.customer.query;

import com.diogomendes.algashop.ordering.domain.model.commons.Email;
import com.diogomendes.algashop.ordering.domain.model.commons.FullName;
import com.diogomendes.algashop.ordering.domain.model.customer.Customer;
import com.diogomendes.algashop.ordering.domain.model.customer.CustomerId;
import com.diogomendes.algashop.ordering.domain.model.customer.Customers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.existingCustomer;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CustomerQueryServiceIT {

    @Autowired
    private CustomerQueryService queryService;

    @Autowired
    private Customers customers;

    @Test
    public void shouldFindById() {
        Customer customer = existingCustomer().build();
        customers.add(customer);

        CustomerOutput output = queryService.findById(customer.id().value());

        assertThat(output)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getEmail
                ).containsExactly(
                        customer.id().value(),
                        customer.fullName().firstName(),
                        customer.email().value()
                );
    }

    @Test
    public void shouldFilterByPage() {
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Diogo", "Mendes")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Thaeme", "Marioto")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Sofia", "Carson")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Sabrina", "Carpenter")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Vasco", "Gama")).build());

        CustomerFilter filter = new CustomerFilter(2, 0);
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByFirstName() {
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Diogo", "Marioto")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Sofia", "Carson")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Diogo", "Mendes")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("diogo");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(CustomerSummaryOutput::getFirstName)
                .containsOnly("Diogo");
    }

    @Test
    public void shouldFilterByEmail() {
        customers.add(existingCustomer().id(new CustomerId()).email(new Email("diogomendes@test.com")).build());
        customers.add(existingCustomer().id(new CustomerId()).email(new Email("sabrinatest2@algashop.com")).build());
        customers.add(existingCustomer().id(new CustomerId()).email(new Email("sofiacarson@test.com")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setEmail("test");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent())
                .extracting(CustomerSummaryOutput::getEmail)
                .containsExactlyInAnyOrder("diogomendes@test.com", "sabrinatest2@algashop.com", "sofiacarson@test.com");
    }

    @Test
    public void shouldFilterByMiltipleParams() {
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Diogo", "Mendes")).email(new Email("diogomendes@email.com")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Sabrina", "Carpenter")).email(new Email("sabrina@email.com")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Thaeme", "Marito")).email(new Email("thaeme@email.com")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("diogo");
        filter.setEmail("mendes");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Diogo");
        assertThat(page.getContent().getFirst().getEmail()).isEqualTo("diogomendes@email.com");
    }

    @Test
    public void shouldOrderByFirstNameAsc() {
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Thaeme", "Marioto")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Sofia", "Carson")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Diogo", "Mendes")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Diogo");
    }

    @Test
    public void shouldOrderByFirstNameDesc() {
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Thaeme", "Marioto")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Sofia", "Carson")).build());
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Diogo", "Mendes")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.DESC);

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Thaeme");
    }

    @Test
    public void givenNonMatchingFilter_shouldReturnEmptyPage() {
        customers.add(existingCustomer().id(new CustomerId()).fullName(new FullName("Thaeme", "Marioto")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("NonExistingName");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getTotalElements()).isEqualTo(0);
    }

}