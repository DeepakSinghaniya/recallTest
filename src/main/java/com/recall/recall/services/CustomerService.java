package com.recall.recall.services;

import com.recall.recall.entity.Customer;
import com.recall.recall.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final Validator validator;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, Validator validator) {
        this.customerRepository = customerRepository;
        this.validator = validator;
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
    }
    public String deleteCustomer(Long id) {
        Optional<Customer> customer = getCustomerById(id);
        if (customer.isPresent()) {
            customerRepository.delete(customer.get());
            return "Customer with id " + id + " deleted successfully";
        } else {
            return "Customer with id " + id + " not found";
        }
    }
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Optional<Customer> optionalCustomer = getCustomerById(id);
        if (optionalCustomer.isEmpty()) {
            throw new EntityNotFoundException("Customer with id " + id + " not found");
        }
        Customer existingCustomer = optionalCustomer.get();
        if(updatedCustomer.getName() != null)
            existingCustomer.setName(updatedCustomer.getName());
        if(updatedCustomer.getEmail() != null)
            existingCustomer.setEmail(updatedCustomer.getEmail());
        Set<ConstraintViolation<Customer>> violations = validator.validate(existingCustomer);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return customerRepository.save(existingCustomer);
    }

}
