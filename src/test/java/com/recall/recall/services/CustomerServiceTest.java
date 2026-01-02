package com.recall.recall.services;

import com.recall.recall.dto.CustomerRequestDTO;
import com.recall.recall.dto.CustomerResponseDTO;
import com.recall.recall.entity.Customer;
import com.recall.recall.repository.CustomerRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
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

        Optional<CustomerResponseDTO> result = customerService.getCustomerById(1L);
        CustomerResponseDTO fetchedCustomer = result.orElse(null);
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

        Page<CustomerResponseDTO> result = customerService.getAllCustomers(
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
        CustomerRequestDTO customerToCreate = CustomerRequestDTO.builder().email("test@fake.com").name("test").build();

        Customer savedCustomer = Customer.builder().id(3L).email("test@fake.com").name("test").createdAt(now).build();

        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        CustomerResponseDTO result = customerService.createCustomer(customerToCreate);

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
        Customer customer = Customer.builder().id(1L).email("test@fake.com").name("test").createdAt(now).build();

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    @DisplayName("delete customer - customer not found")
    public void shouldReturnNotFoundWhenDeletingNonExistentCustomer() {
        when(customerRepository.findById(99L))
            .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> customerService.deleteCustomer(99L));

        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    @DisplayName("update customer - success")
    public void shouldUpdateCustomer() {
        LocalDateTime now = LocalDateTime.now();
        Customer existingCustomer = Customer.builder().id(1L).email("test@fake.com").name("test").createdAt(now).build();

        CustomerRequestDTO updatedCustomer = CustomerRequestDTO.builder().id(1L).name("test1").email("test@fake.com").build();

        Customer savedCustomer = Customer.builder().id(1L).name("test2").email("test2@fake.com").createdAt(now).build();

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        CustomerResponseDTO result = customerService.updateCustomer( updatedCustomer);

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
        Customer existingCustomer = Customer.builder().id(1L).email("test@fake.com").name("test").createdAt(now).build();

        CustomerRequestDTO updatedCustomer = CustomerRequestDTO.builder().id(1L).name("test1").email(null).build();

        Customer savedCustomer = Customer.builder().id(1L).name("test2").email("test2@fake.com").createdAt(now).build();

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        CustomerResponseDTO result = customerService.updateCustomer(updatedCustomer);

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

        CustomerRequestDTO updatedCustomer = CustomerRequestDTO.builder().id(1L).name(null).email("newemail@fake.com").build();

        Customer savedCustomer = Customer.builder().id(1L).name("test").email("newemail@fake.com").createdAt(now).build();

        when(customerRepository.findById(1L))
            .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class)))
            .thenReturn(savedCustomer);

        CustomerResponseDTO result = customerService.updateCustomer(updatedCustomer);

        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals("newemail@fake.com", result.getEmail());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("update customer - customer not found")
    public void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
        CustomerRequestDTO updatedCustomer = CustomerRequestDTO.builder().id(99L).name("test").email("test@fake.com").build();

        when(customerRepository.findById(99L))
            .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> customerService.updateCustomer(updatedCustomer));

        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
