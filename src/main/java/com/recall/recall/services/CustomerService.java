package com.recall.recall.services;

import com.recall.recall.dto.CustomerMapper;
import com.recall.recall.dto.CustomerRequestDTO;
import com.recall.recall.dto.CustomerResponseDTO;
import com.recall.recall.entity.Customer;
import com.recall.recall.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public Optional<CustomerResponseDTO> getCustomerById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(customerMapper::toResponseDTO);
    }
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toResponseDTO);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerMapper.toEntity(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return  customerMapper.toResponseDTO(savedCustomer);
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
    }
    public String deleteCustomer(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);;
        if (customer.isPresent()) {
            customerRepository.delete(customer.get());
            return "Customer with id " + id + " deleted successfully";
        } else {
            return "Customer with id " + id + " not found";
        }
    }
    @Transactional
    public CustomerResponseDTO updateCustomer(CustomerRequestDTO customerRequestDTO) {
        Long id = customerRequestDTO.getId();
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new EntityNotFoundException("Customer with id " + id + " not found");
        }
        Customer existingCustomer = optionalCustomer.get();
        if(customerRequestDTO.getName() != null)
            existingCustomer.setName(customerRequestDTO.getName());
        if(customerRequestDTO.getEmail() != null)
            existingCustomer.setEmail(customerRequestDTO.getEmail());
        Customer customer = customerRepository.save(existingCustomer);
        return customerMapper.toResponseDTO(customer);
    }

}
