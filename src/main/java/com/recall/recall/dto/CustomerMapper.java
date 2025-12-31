package com.recall.recall.dto;

import com.recall.recall.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }

    public Customer toEntity(CustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setName(requestDTO.getName());
        customer.setEmail(requestDTO.getEmail());
        customer.setId(requestDTO.getId());
        return customer;
    }

    public void updateEntityFromDTO(Customer customer, CustomerRequestDTO requestDTO) {
        if (customer == null || requestDTO == null) {
            return;
        }

        if(requestDTO.getId() != null){
            customer.setId(requestDTO.getId());
        }
        if (requestDTO.getName() != null) {
            customer.setName(requestDTO.getName());
        }
        if (requestDTO.getEmail() != null) {
            customer.setEmail(requestDTO.getEmail());
        }
    }
}

