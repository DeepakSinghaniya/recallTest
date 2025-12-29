package com.recall.recall.controller;

import com.recall.recall.entity.Customer;
import com.recall.recall.services.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    private Customer buildCustomer(Long id, String name, String email, LocalDateTime createdAt) {
        Customer c = new Customer();
        c.setId(id);
        c.setName(name);
        c.setEmail(email);
        c.setCreatedAt(createdAt);
        return c;
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} returns customer when found")
    void getCustomerById_found() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Customer customer = buildCustomer(1L, "test", "test@fake.com", now);
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@fake.com")));

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} returns 404 when not found")
    void getCustomerById_notFound() throws Exception {
        when(customerService.getCustomerById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(99L);
    }

    @Test
    @DisplayName("GET /api/v1/customers returns paged list")
    void getAllCustomers() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Customer c1 = buildCustomer(1L, "test", "test@fake.com", now);
        Customer c2 = buildCustomer(2L, "test1", "test1@fake.com", now);
        Page<Customer> page = new PageImpl<>(List.of(c1, c2), PageRequest.of(0, 10), 2);
        when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/customers?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("test")))
                .andExpect(jsonPath("$.content[1].name", is("test1")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));

        verify(customerService, times(1)).getAllCustomers(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/v1/customers creates customer")
    void createCustomer() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Customer saved = buildCustomer(1L, "test", "test@fake.com", now);
        when(customerService.createCustomer(any(Customer.class))).thenReturn(saved);

        String json = "{\"name\":\"test\",\"email\":\"test@fake.com\"}";

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@fake.com")));

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("PUT /api/v1/customers/{id} updates customer")
    void updateCustomer() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Customer updated = buildCustomer(1L, "test-updated", "test-updated@fake.com", now);
        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(updated);

        String json = "{\"name\":\"test-updated\",\"email\":\"test-updated@fake.com\"}";

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test-updated")))
                .andExpect(jsonPath("$.email", is("test-updated@fake.com")));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/customers/{id} deletes customer")
    void deleteCustomer() throws Exception {
        when(customerService.deleteCustomer(1L)).thenReturn("Customer with id 1 deleted successfully");

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer with id 1 deleted successfully"));

        verify(customerService, times(1)).deleteCustomer(1L);
    }
}
