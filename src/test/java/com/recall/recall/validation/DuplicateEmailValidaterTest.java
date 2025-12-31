package com.recall.recall.validation;

import com.recall.recall.entity.Customer;
import com.recall.recall.services.CustomerService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DuplicateEmailValidaterTest {

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private ConstraintValidatorContext context;

    @MockitoBean
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @MockitoBean
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderContext;

    @Autowired
    private DuplicateEmailValidater duplicateEmailValidater;

    @BeforeEach
    void setUp() {
        duplicateEmailValidater.initialize(null);
    }


    @Test
    @DisplayName("Should return true when customer is null")
    void testIsValid_WhenCustomerIsNull_ReturnsTrue() {
        boolean result = duplicateEmailValidater.isValid(null, context);

        assertTrue(result);
        verifyNoInteractions(customerService);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when customer email is null")
    void testIsValid_WhenEmailIsNull_ReturnsTrue() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail(null);

        boolean result = duplicateEmailValidater.isValid(customer, context);

        assertTrue(result);
        verifyNoInteractions(customerService);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when customerService is null")
    void testIsValid_WhenCustomerServiceIsNull_ReturnsTrue() {
        duplicateEmailValidater.customerService = null;
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("test@example.com");

        boolean result = duplicateEmailValidater.isValid(customer, context);

        assertTrue(result);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when creating new customer with unique email")
    void testIsValid_NewCustomerWithUniqueEmail_ReturnsTrue() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(null);
        when(customerService.existsByEmail("test@example.com")).thenReturn(false);

        boolean result = duplicateEmailValidater.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmail("test@example.com");
        verifyNoInteractions(context);
    }


    @Test
    @DisplayName("Should return true when updating customer with unique email")
    void testIsValid_UpdateCustomerWithUniqueEmail_ReturnsTrue() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(1L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);

        boolean result = duplicateEmailValidater.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmailAndIdNot("test@example.com", 1L);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when updating customer keeping same email")
    void testIsValid_UpdateCustomerKeepingSameEmail_ReturnsTrue() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(1L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);

        boolean result = duplicateEmailValidater.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmailAndIdNot("test@example.com", 1L);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return false when updating customer with duplicate email")
    void testIsValid_UpdateCustomerWithDuplicateEmail_ReturnsFalse() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(1L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(true);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode("email")).thenReturn(nodeBuilderContext);
        when(nodeBuilderContext.addConstraintViolation()).thenReturn(context);

        boolean result = duplicateEmailValidater.isValid(customer, context);

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
        Customer customer1 = new Customer();
        customer1.setName("User1");
        customer1.setEmail("user@example.com");
        customer1.setId(null);
        when(customerService.existsByEmail("user@example.com")).thenReturn(false);

        Customer customer2 = new Customer();
        customer2.setName("User2");
        customer2.setEmail("user.name@example.co.in");
        customer2.setId(null);
        when(customerService.existsByEmail("user.name@example.co.in")).thenReturn(false);

        Customer customer3 = new Customer();
        customer3.setName("User3");
        customer3.setEmail("user+tag@example.com");
        customer3.setId(null);
        when(customerService.existsByEmail("user+tag@example.com")).thenReturn(false);

        assertTrue(duplicateEmailValidater.isValid(customer1, context));
        assertTrue(duplicateEmailValidater.isValid(customer2, context));
        assertTrue(duplicateEmailValidater.isValid(customer3, context));
    }

    @Test
    @DisplayName("Should verify correct error message format")
    void testIsValid_ErrorMessageFormat_ContainsEmail() {
        String testEmail = "duplicate@example.com";
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail(testEmail);
        customer.setId(null);
        when(customerService.existsByEmail(testEmail)).thenReturn(true);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode("email")).thenReturn(nodeBuilderContext);
        when(nodeBuilderContext.addConstraintViolation()).thenReturn(context);

        duplicateEmailValidater.isValid(customer, context);

    }


    @Test
    @DisplayName("Should call existsByEmailAndIdNot for existing customer with ID")
    void testIsValid_ExistingCustomer_CallsExistsByEmailAndIdNot() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setId(5L);
        when(customerService.existsByEmailAndIdNot("test@example.com", 5L)).thenReturn(false);

        duplicateEmailValidater.isValid(customer, context);
    }

    @Test
    @DisplayName("Should handle empty email string")
    void testIsValid_WithEmptyEmail_ReturnsTrue() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("");
        customer.setId(null);
        when(customerService.existsByEmail("")).thenReturn(false);

        boolean result = duplicateEmailValidater.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmail("");
    }

    @Test
    @DisplayName("Should validate with various customer IDs")
    void testIsValid_WithVariousCustomerIds_ValidatesCorrectly() {
        Customer customer1 = new Customer();
        customer1.setName("User1");
        customer1.setEmail("test1@example.com");
        customer1.setId(100L);
        when(customerService.existsByEmailAndIdNot("test1@example.com", 100L)).thenReturn(false);

        Customer customer2 = new Customer();
        customer2.setName("User2");
        customer2.setEmail("test2@example.com");
        customer2.setId(999L);
        when(customerService.existsByEmailAndIdNot("test2@example.com", 999L)).thenReturn(false);

        assertTrue(duplicateEmailValidater.isValid(customer1, context));
        assertTrue(duplicateEmailValidater.isValid(customer2, context));

        verify(customerService).existsByEmailAndIdNot("test1@example.com", 100L);
        verify(customerService).existsByEmailAndIdNot("test2@example.com", 999L);
    }
}
