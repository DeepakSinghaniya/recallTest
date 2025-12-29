package com.recall.recall.services;

import com.recall.recall.entity.Customer;
import com.recall.recall.repository.CustomerRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class CustomerServiceTest {
    @MockitoBean
    private CustomerRepository customerRepository;
    @MockitoBean
    private Validator validator;

    @Autowired
    private CustomerService customerService;

    @Test
    @DisplayName("get customer by id - success")
    public void shouldGetCustomerById() {
        LocalDateTime now = LocalDateTime.now();
        Customer customer = new Customer();
        customer.setEmail("test@fake.com");
        customer.setName("test");
        customer.setId(1L);
        customer.setCreatedAt(now);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(1L);
        Customer fetchedCustomer = result.orElse(null);
        assertNotNull(fetchedCustomer);
        assertEquals("test", fetchedCustomer.getName());
        assertEquals("test@fake.com", fetchedCustomer.getEmail());
        assertEquals(1L, fetchedCustomer.getId());
        assertEquals(now, fetchedCustomer.getCreatedAt());
        verify(customerRepository, times(1)).findById(1L);

        assertNotNull( fetchedCustomer.getName());
        assertNotNull( fetchedCustomer.getEmail());
        assertNotNull( fetchedCustomer.getId());
        assertNotNull( fetchedCustomer.getCreatedAt());

    }

    @Test
    @DisplayName("get all customers - success")
    public void shouldGetAllCustomers() {
        LocalDateTime now = LocalDateTime.now();
        Customer customer1 = new Customer();
        customer1.setEmail("test@fake.com");
        customer1.setName("test");
        customer1.setId(1L);
        customer1.setCreatedAt(now);
        Customer customer2 = new Customer();
        customer2.setEmail("test1@fake.com");
        customer2.setName("test1");
        customer2.setId(2L);
        customer2.setCreatedAt(now);

        PageImpl<Customer> page = new PageImpl<>(
            List.of(customer1, customer2)
        );

        when(customerRepository.findAll(any(Pageable.class)))
            .thenReturn(page);

        Page<Customer> result = customerService.getAllCustomers(
            PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("test", result.getContent().get(0).getName());
        assertEquals("test1", result.getContent().get(1).getName());
        assertEquals("test@fake.com", result.getContent().get(0).getEmail());
        assertEquals("test1@fake.com", result.getContent().get(1).getEmail());
        verify(customerRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("create customer - success")
    public void shouldCreateCustomer() {
        LocalDateTime now = LocalDateTime.now();
        Customer customerToCreate = new Customer();
        customerToCreate.setEmail("test@fake.com");
        customerToCreate.setName("test");

        Customer savedCustomer = new Customer();
        savedCustomer.setId(3L);
        savedCustomer.setEmail("test@fake.com");
        savedCustomer.setName("test");
        savedCustomer.setCreatedAt(now);

        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        Customer result = customerService.createCustomer(customerToCreate);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("test", result.getName());
        assertEquals("test@fake.com", result.getEmail());
        assertEquals(now, result.getCreatedAt());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("exists by email - email exists")
    public void shouldReturnTrueWhenEmailExists() {
        String email = "test@fake.com";

        when(customerRepository.existsByEmail(email))
            .thenReturn(true);

        boolean result = customerService.existsByEmail(email);

        assertTrue(result);
        verify(customerRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("exists by email - email does not exist")
    public void shouldReturnFalseWhenEmailDoesNotExist() {
        String email = "nonexistent@fake.com";

        when(customerRepository.existsByEmail(email))
            .thenReturn(false);

        boolean result = customerService.existsByEmail(email);

        assertFalse(result);
        verify(customerRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("exists by email and id not - email exists for different customer")
    public void shouldReturnTrueWhenEmailExistsForDifferentCustomer() {
        String email = "test@fake.com";
        Long customerId = 1L;

        when(customerRepository.existsByEmailAndIdNot(email, customerId))
            .thenReturn(true);

        boolean result = customerService.existsByEmailAndIdNot(email, customerId);

        assertTrue(result);
        verify(customerRepository, times(1)).existsByEmailAndIdNot(email, customerId);
    }

    @Test
    @DisplayName("exists by email and id not - email does not exist")
    public void shouldReturnFalseWhenEmailDoesNotExistForAnyCustomer() {
        String email = "nonexistent@fake.com";
        Long customerId = 1L;

        when(customerRepository.existsByEmailAndIdNot(email, customerId))
            .thenReturn(false);

        boolean result = customerService.existsByEmailAndIdNot(email, customerId);

        assertFalse(result);
        verify(customerRepository, times(1)).existsByEmailAndIdNot(email, customerId);
    }

    @Test
    @DisplayName("exists by email and id not - email belongs to same customer")
    public void shouldReturnFalseWhenEmailBelongsToSameCustomer() {
        String email = "test@fake.com";
        Long customerId = 1L;

        when(customerRepository.existsByEmailAndIdNot(email, customerId))
            .thenReturn(false);

        boolean result = customerService.existsByEmailAndIdNot(email, customerId);

        assertFalse(result);
        verify(customerRepository, times(1)).existsByEmailAndIdNot(email, customerId);
    }

    @Test
    @DisplayName("delete customer - success")
    public void shouldDeleteCustomer() {
        LocalDateTime now = LocalDateTime.now();
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@fake.com");
        customer.setName("test");
        customer.setCreatedAt(now);

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(customer));

        String result = customerService.deleteCustomer(1L);

        assertEquals("Customer with id 1 deleted successfully", result);
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    @DisplayName("delete customer - customer not found")
    public void shouldReturnNotFoundWhenDeletingNonExistentCustomer() {
        when(customerRepository.findById(99L))
            .thenReturn(Optional.empty());

        String result = customerService.deleteCustomer(99L);

        assertEquals("Customer with id 99 not found", result);
        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    @DisplayName("update customer - success")
    public void shouldUpdateCustomer() {
        LocalDateTime now = LocalDateTime.now();
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setEmail("test@fake.com");
        existingCustomer.setName("test");
        existingCustomer.setCreatedAt(now);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("test1");
        updatedCustomer.setEmail("test@fake.com");

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName("test2");
        savedCustomer.setEmail("test2@fake.com");
        savedCustomer.setCreatedAt(now);

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        Customer result = customerService.updateCustomer(1L, updatedCustomer);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test2", result.getName());
        assertEquals("test2@fake.com", result.getEmail());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("update customer - update only name")
    public void shouldUpdateCustomerNameOnly() {
        LocalDateTime now = LocalDateTime.now();
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setEmail("test@fake.com");
        existingCustomer.setName("test");
        existingCustomer.setCreatedAt(now);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("test1");
        updatedCustomer.setEmail(null);

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName("test2");
        savedCustomer.setEmail("test2@fake.com");
        savedCustomer.setCreatedAt(now);

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        Customer result = customerService.updateCustomer(1L, updatedCustomer);

        assertNotNull(result);
        assertEquals("test2", result.getName());
        assertEquals("test2@fake.com", result.getEmail());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("update customer - update only email")
    public void shouldUpdateCustomerEmailOnly() {
        LocalDateTime now = LocalDateTime.now();
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setEmail("test@fake.com");
        existingCustomer.setName("test");
        existingCustomer.setCreatedAt(now);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName(null);
        updatedCustomer.setEmail("newemail@fake.com");

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName("test");
        savedCustomer.setEmail("newemail@fake.com");
        savedCustomer.setCreatedAt(now);

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        Customer result = customerService.updateCustomer(1L, updatedCustomer);

        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals("newemail@fake.com", result.getEmail());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("update customer - customer not found")
    public void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("test");
        updatedCustomer.setEmail("test@fake.com");

        when(customerRepository.findById(99L))
            .thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
            () -> customerService.updateCustomer(99L, updatedCustomer));

        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("update customer - throw exception")
    public void shouldThrowExceptionWhenDataNotValid() {
        LocalDateTime now = LocalDateTime.now();
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setEmail("test@fake.com");
        existingCustomer.setName("test");
        existingCustomer.setCreatedAt(now);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("");
        updatedCustomer.setEmail("invalid-email");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(existingCustomer));
        when(validator.validate(any(Customer.class)))
                .thenReturn(Set.of(mock(jakarta.validation.ConstraintViolation.class)));

        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> customerService.updateCustomer(1L, updatedCustomer));

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

}
