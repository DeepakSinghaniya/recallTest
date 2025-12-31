package com.recall.recall.validation;

import com.recall.recall.dto.CustomerRequestDTO;
import com.recall.recall.dto.CustomerResponseDTO;
import com.recall.recall.entity.Customer;
import com.recall.recall.services.CustomerService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DuplicateEmailValidatorTest {

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private ConstraintValidatorContext context;

    @MockitoBean
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @MockitoBean
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderContext;

    @Autowired
    private DuplicateEmailValidator duplicateEmailValidator;

    @BeforeEach
    void setUp() {
        duplicateEmailValidator.initialize(null);
    }


    @Test
    @DisplayName("Should return true when customer is null")
    void testIsValid_WhenCustomerIsNull_ReturnsTrue() {
        boolean result = duplicateEmailValidator.isValid(null, context);

        assertTrue(result);
        verifyNoInteractions(customerService);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when customer email is null")
    void testIsValid_WhenEmailIsNull_ReturnsTrue() {
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setName("Test User");
        customerRequestDTO.setEmail(null);

        boolean result = duplicateEmailValidator.isValid(customerRequestDTO, context);

        assertTrue(result);
        verifyNoInteractions(customerService);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when customerService is null")
    void testIsValid_WhenCustomerServiceIsNull_ReturnsTrue() {
        DuplicateEmailValidator duplicateEmailValidator = new DuplicateEmailValidator(null);

        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setName("Test User");
        customerRequestDTO.setEmail("test@example.com");

        boolean result = duplicateEmailValidator.isValid(customerRequestDTO, context);

        assertTrue(result);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when creating new customer with unique email")
    void testIsValid_NewCustomerWithUniqueEmail_ReturnsTrue() {
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setName("Test User");
        customerRequestDTO.setEmail("test@example.com");
        customerRequestDTO.setId(null);
        when(customerService.existsByEmail("test@example.com")).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customerRequestDTO, context);

        assertTrue(result);
        verify(customerService).existsByEmail("test@example.com");
        verifyNoInteractions(context);
    }


    @Test
    @DisplayName("Should return true when updating customer with unique email")
    void testIsValid_UpdateCustomerWithUniqueEmail_ReturnsTrue() {
        CustomerRequestDTO customer = new CustomerRequestDTO();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(1L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmailAndIdNot("test@example.com", 1L);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when updating customer keeping same email")
    void testIsValid_UpdateCustomerKeepingSameEmail_ReturnsTrue() {
        CustomerRequestDTO customer = new CustomerRequestDTO();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(1L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmailAndIdNot("test@example.com", 1L);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return false when updating customer with duplicate email")
    void testIsValid_UpdateCustomerWithDuplicateEmail_ReturnsFalse() {
        CustomerRequestDTO customer = new CustomerRequestDTO();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(1L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(true);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode("email")).thenReturn(nodeBuilderContext);
        when(nodeBuilderContext.addConstraintViolation()).thenReturn(context);

        boolean result = duplicateEmailValidator.isValid(customer, context);

        assertFalse(result);
        verify(customerService).existsByEmailAndIdNot("test@example.com", 1L);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Email test@example.com already exists!");
        verify(constraintViolationBuilder).addPropertyNode("email");
        verify(nodeBuilderContext).addConstraintViolation();
    }

    @Test
    @DisplayName("Should handle different email formats correctly")
    void testIsValid_WithDifferentEmailFormats_ValidatesCorrectly() {
        CustomerRequestDTO customer1 = new CustomerRequestDTO();
        customer1.setName("User1");
        customer1.setEmail("user@example.com");
        customer1.setId(null);
        when(customerService.existsByEmail("user@example.com")).thenReturn(false);

        CustomerRequestDTO customer2 = new CustomerRequestDTO();
        customer2.setName("User2");
        customer2.setEmail("user.name@example.co.in");
        customer2.setId(null);
        when(customerService.existsByEmail("user.name@example.co.in")).thenReturn(false);

        CustomerRequestDTO customer3 = new CustomerRequestDTO();
        customer3.setName("User3");
        customer3.setEmail("user+tag@example.com");
        customer3.setId(null);
        when(customerService.existsByEmail("user+tag@example.com")).thenReturn(false);

        assertTrue(duplicateEmailValidator.isValid(customer1, context));
        assertTrue(duplicateEmailValidator.isValid(customer2, context));
        assertTrue(duplicateEmailValidator.isValid(customer3, context));
    }

    @Test
    @DisplayName("Should verify correct error message format")
    void testIsValid_ErrorMessageFormat_ContainsEmail() {
        String testEmail = "duplicate@example.com";
        CustomerRequestDTO customer = new CustomerRequestDTO();
        customer.setName("Test User");
        customer.setEmail(testEmail);
        customer.setId(null);
        when(customerService.existsByEmail(testEmail)).thenReturn(true);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode("email")).thenReturn(nodeBuilderContext);
        when(nodeBuilderContext.addConstraintViolation()).thenReturn(context);

        duplicateEmailValidator.isValid(customer, context);

    }


    @Test
    @DisplayName("Should call existsByEmailAndIdNot for existing customer with ID")
    void testIsValid_ExistingCustomer_CallsExistsByEmailAndIdNot() {
        CustomerRequestDTO customer = new CustomerRequestDTO();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(5L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 5L)).thenReturn(false);

        duplicateEmailValidator.isValid(customer, context);
    }

    @Test
    @DisplayName("Should handle empty email string")
    void testIsValid_WithEmptyEmail_ReturnsTrue() {
        CustomerRequestDTO customer = new CustomerRequestDTO();
        customer.setName("Test User");
        customer.setEmail("");
        customer.setId(null);
        when(customerService.existsByEmail("")).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmail("");
    }

    @Test
    @DisplayName("Should validate with various customer IDs")
    void testIsValid_WithVariousCustomerIds_ValidatesCorrectly() {
        CustomerRequestDTO customer1 = new CustomerRequestDTO();
        customer1.setName("User1");
        customer1.setEmail("test1@example.com");
        customer1.setId(100L);
        when(customerService.existsByEmailAndIdNot("test1@example.com", 100L)).thenReturn(false);

        CustomerRequestDTO customer2 = new CustomerRequestDTO();
        customer2.setName("User2");
        customer2.setEmail("test2@example.com");
        customer2.setId(999L);
        when(customerService.existsByEmailAndIdNot("test2@example.com", 999L)).thenReturn(false);

        assertTrue(duplicateEmailValidator.isValid(customer1, context));
        assertTrue(duplicateEmailValidator.isValid(customer2, context));

        verify(customerService).existsByEmailAndIdNot("test1@example.com", 100L);
        verify(customerService).existsByEmailAndIdNot("test2@example.com", 999L);
    }
}
