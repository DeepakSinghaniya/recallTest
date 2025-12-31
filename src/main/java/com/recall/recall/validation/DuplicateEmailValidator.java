package com.recall.recall.validation;

import com.recall.recall.dto.CustomerRequestDTO;
import com.recall.recall.services.CustomerService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DuplicateEmailValidator implements ConstraintValidator<DuplicateEmail, CustomerRequestDTO> {
    private final CustomerService customerService;

    @Override
    public void initialize(DuplicateEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(CustomerRequestDTO customerRequestDTO, ConstraintValidatorContext context) {
        if (customerService == null || customerRequestDTO == null || customerRequestDTO.getEmail() == null) {
            return true;
        }
        Long id = customerRequestDTO.getId();
        String email = customerRequestDTO.getEmail();
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
