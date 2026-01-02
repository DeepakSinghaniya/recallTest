package com.recall.recall.dto;

import com.recall.recall.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerResponseDTO.builder().id(customer.getId()).name(customer.getName()).email(customer.getEmail()).createdAt(customer.getCreatedAt()).build();
    }

    public Customer toEntity(CustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        return Customer.builder().id(requestDTO.getId()).name(requestDTO.getName()).email(requestDTO.getEmail()).build();
    }
}

