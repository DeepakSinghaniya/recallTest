package com.recall.recall.services;

import com.recall.recall.dto.CustomerRequestDTO;
import com.recall.recall.dto.CustomerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerService {

    Optional<CustomerResponseDTO> getCustomerById(Long id);

    Page<CustomerResponseDTO> getAllCustomers(Pageable pageable);

    CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    void deleteCustomer(Long id);

    CustomerResponseDTO updateCustomer(CustomerRequestDTO customerRequestDTO);
}
