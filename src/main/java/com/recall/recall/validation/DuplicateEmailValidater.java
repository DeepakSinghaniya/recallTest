package com.recall.recall.validation;

import com.recall.recall.entity.Customer;
import com.recall.recall.services.CustomerService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class DuplicateEmailValidater implements ConstraintValidator<DuplicateEmail, Customer> {
    public CustomerService customerService;
    DuplicateEmailValidater(CustomerService customerService){
        this.customerService = customerService;
    }

    @Override
    public void initialize(DuplicateEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(Customer customer, ConstraintValidatorContext context) {
        if (customerService == null || customer == null || customer.getEmail() == null) {
            return true;
        }
        Long id = customer.getId();
        String email = customer.getEmail();
        boolean isDuplicate;
        if (id == null) {
            isDuplicate = customerService.existsByEmail(email);
        } else {
            isDuplicate = customerService.existsByEmailAndIdNot(email, id);
        }
        if (isDuplicate) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email " + email + " already exists!")
                    .addPropertyNode("email").addConstraintViolation();
            return false;
        }
        return true;
    }
}
