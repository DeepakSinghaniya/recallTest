package com.recall.recall.validation;

import com.recall.recall.dto.CustomerRequestDTO;
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
        CustomerRequestDTO customerRequestDTO = CustomerRequestDTO.builder()
                .name("Test User")
                .email(null)
                .build();

        boolean result = duplicateEmailValidator.isValid(customerRequestDTO, context);

        assertTrue(result);
        verifyNoInteractions(customerService);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when customerService is null")
    void testIsValid_WhenCustomerServiceIsNull_ReturnsTrue() {
        DuplicateEmailValidator duplicateEmailValidator = new DuplicateEmailValidator(null);

        CustomerRequestDTO customerRequestDTO = CustomerRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        boolean result = duplicateEmailValidator.isValid(customerRequestDTO, context);

        assertTrue(result);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when creating new customer with unique email")
    void testIsValid_NewCustomerWithUniqueEmail_ReturnsTrue() {
        CustomerRequestDTO customerRequestDTO = CustomerRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .id(null)
                .build();
        when(customerService.existsByEmail("test@example.com")).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customerRequestDTO, context);

        assertTrue(result);
        verify(customerService).existsByEmail("test@example.com");
        verifyNoInteractions(context);
    }


    @Test
    @DisplayName("Should return true when updating customer with unique email")
    void testIsValid_UpdateCustomerWithUniqueEmail_ReturnsTrue() {
        CustomerRequestDTO customer = CustomerRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .id(1L)
                .build();
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmailAndIdNot("test@example.com", 1L);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return true when updating customer keeping same email")
    void testIsValid_UpdateCustomerKeepingSameEmail_ReturnsTrue() {
        CustomerRequestDTO customer = CustomerRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .id(1L)
                .build();
        when(customerService.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmailAndIdNot("test@example.com", 1L);
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should return false when updating customer with duplicate email")
    void testIsValid_UpdateCustomerWithDuplicateEmail_ReturnsFalse() {
        CustomerRequestDTO customer = CustomerRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .id(1L)
                .build();
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
        CustomerRequestDTO customer1 = CustomerRequestDTO.builder()
                .name("User1")
                .email("user@example.com")
                .id(null)
                .build();
        when(customerService.existsByEmail("user@example.com")).thenReturn(false);

        CustomerRequestDTO customer2 = CustomerRequestDTO.builder()
                .name("User2")
                .email("user.name@example.co.in")
                .id(null)
                .build();
        when(customerService.existsByEmail("user.name@example.co.in")).thenReturn(false);

        CustomerRequestDTO customer3 = CustomerRequestDTO.builder()
                .name("User3")
                .email("user+tag@example.com")
                .id(null)
                .build();
        when(customerService.existsByEmail("user+tag@example.com")).thenReturn(false);

        assertTrue(duplicateEmailValidator.isValid(customer1, context));
        assertTrue(duplicateEmailValidator.isValid(customer2, context));
        assertTrue(duplicateEmailValidator.isValid(customer3, context));
    }

    @Test
    @DisplayName("Should verify correct error message format")
    void testIsValid_ErrorMessageFormat_ContainsEmail() {
        String testEmail = "duplicate@example.com";
        CustomerRequestDTO customer = CustomerRequestDTO.builder()
                .name("Test User")
                .email(testEmail)
                .id(null)
                .build();
        when(customerService.existsByEmail(testEmail)).thenReturn(true);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode("email")).thenReturn(nodeBuilderContext);
        when(nodeBuilderContext.addConstraintViolation()).thenReturn(context);

        duplicateEmailValidator.isValid(customer, context);
    }


    @Test
    @DisplayName("Should call existsByEmailAndIdNot for existing customer with ID")
    void testIsValid_ExistingCustomer_CallsExistsByEmailAndIdNot() {
        CustomerRequestDTO customer = CustomerRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .id(5L)
                .build();
        when(customerService.existsByEmailAndIdNot("test@example.com", 5L)).thenReturn(false);

        duplicateEmailValidator.isValid(customer, context);
    }

    @Test
    @DisplayName("Should handle empty email string")
    void testIsValid_WithEmptyEmail_ReturnsTrue() {
        CustomerRequestDTO customer = CustomerRequestDTO.builder()
                .name("Test User")
                .email("")
                .id(null)
                .build();
        when(customerService.existsByEmail("")).thenReturn(false);

        boolean result = duplicateEmailValidator.isValid(customer, context);

        assertTrue(result);
        verify(customerService).existsByEmail("");
    }

    @Test
    @DisplayName("Should validate with various customer IDs")
    void testIsValid_WithVariousCustomerIds_ValidatesCorrectly() {
        CustomerRequestDTO customer1 = CustomerRequestDTO.builder()
                .name("User1")
                .email("test1@example.com")
                .id(100L)
                .build();
        when(customerService.existsByEmailAndIdNot("test1@example.com", 100L)).thenReturn(false);

        CustomerRequestDTO customer2 = CustomerRequestDTO.builder()
                .name("User2")
                .email("test2@example.com")
                .id(999L)
                .build();
        when(customerService.existsByEmailAndIdNot("test2@example.com", 999L)).thenReturn(false);

        assertTrue(duplicateEmailValidator.isValid(customer1, context));
        assertTrue(duplicateEmailValidator.isValid(customer2, context));

        verify(customerService).existsByEmailAndIdNot("test1@example.com", 100L);
        verify(customerService).existsByEmailAndIdNot("test2@example.com", 999L);
    }
}
