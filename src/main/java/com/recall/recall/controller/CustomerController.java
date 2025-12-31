package com.recall.recall.controller;

import com.recall.recall.dto.CustomerRequestDTO;
import com.recall.recall.dto.CustomerResponseDTO;
import com.recall.recall.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RequestMapping("/api/v1/customers")
@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public ResponseEntity<Page<CustomerResponseDTO>> getAllCustomers(Pageable pageable) {
        Page<CustomerResponseDTO> customerResponseDTOs = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customerResponseDTOs);
    }

    @PostMapping("")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO savedCustomer = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.ok(savedCustomer);
    }

    @PutMapping("")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO updated = customerService.updateCustomer(customerRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
           String message = customerService.deleteCustomer(id);
           return ResponseEntity.ok(message);
    }
}
