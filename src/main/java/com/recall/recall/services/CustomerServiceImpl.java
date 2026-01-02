package com.recall.recall.services;

import com.recall.recall.dto.*;
import com.recall.recall.entity.Customer;
import com.recall.recall.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    private Customer getCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer with id {} not found", id);
                    return  new EntityNotFoundException("Customer with id " + id + " not found");
                });
    }

    public Optional<CustomerResponseDTO> getCustomerById(Long id) {
        try {
            Optional<Customer> customer = customerRepository.findById(id);
            return customer.map(customerMapper::toResponseDTO);
        } catch (DataAccessException ex) {
            logger.error("Error retrieving customer with id {}: {}", id, ex.getMessage());
            return Optional.empty();
        }
    }
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        try {
            return customerRepository.findAll(pageable).map(customerMapper::toResponseDTO);
        } catch (DataAccessException ex) {
            logger.error("Error retrieving customers: {}", ex.getMessage());
            return Page.empty();
        }
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        try {
            Customer customer = customerMapper.toEntity(customerRequestDTO);
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("customer successfully created with id {}", savedCustomer.getId());
            return customerMapper.toResponseDTO(savedCustomer);
        } catch (DataAccessException ex) {
            logger.error("Error creating customer: {}", ex.getMessage());
            throw ex;
        }
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
    }
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerOrThrow(id);
        try {
            customerRepository.delete(customer);
            logger.info("Customer deleted with id {}", id);
        } catch (DataAccessException ex) {
            logger.error("Error deleting customer with id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }
    @Transactional
    public CustomerResponseDTO updateCustomer(CustomerRequestDTO customerRequestDTO) {
        Long id = customerRequestDTO.getId();
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            logger.error("Customer with id {} not found for update", id);
            throw new EntityNotFoundException("Customer with id " + id + " not found");
        }
        Customer existingCustomer = optionalCustomer.get();
        if(customerRequestDTO.getName() != null)
            existingCustomer.setName(customerRequestDTO.getName());
        if(customerRequestDTO.getEmail() != null)
            existingCustomer.setEmail(customerRequestDTO.getEmail());
        try {
            Customer customer = customerRepository.save(existingCustomer);
            logger.info("customer successfully updated with id {}", id);
            return customerMapper.toResponseDTO(customer);
        } catch (DataAccessException ex) {
            logger.error("Error updating customer with id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

}
