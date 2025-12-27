package com.recall.recall.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Entity Tests")
public class CustomerTest {

    private Customer customer;
    private LocalDateTime testDateTime;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        testDateTime = LocalDateTime.now();
    }

    @Test
    @DisplayName("create customer with constructor - with parameters")
    public void shouldCreateCustomerWithConstructor() {
        Customer newCustomer = new Customer("test", "test@fake.com");

        assertNotNull(newCustomer);
        assertEquals("test", newCustomer.getName());
        assertEquals("test@fake.com", newCustomer.getEmail());
        assertNull(newCustomer.getId());
        assertNull(newCustomer.getCreatedAt());
    }

    @Test
    @DisplayName("create customer with no-arg constructor")
    public void shouldCreateCustomerWithNoArgConstructor() {
        Customer newCustomer = new Customer();

        assertNotNull(newCustomer);
        assertNull(newCustomer.getId());
        assertNull(newCustomer.getName());
        assertNull(newCustomer.getEmail());
        assertNull(newCustomer.getCreatedAt());
    }

    @Test
    @DisplayName("set and get id")
    public void shouldSetAndGetId() {
        customer.setId(1L);

        assertEquals(1L, customer.getId());
    }

    @Test
    @DisplayName("set and get name")
    public void shouldSetAndGetName() {
        customer.setName("test1");

        assertEquals("test1", customer.getName());
    }

    @Test
    @DisplayName("set and get email")
    public void shouldSetAndGetEmail() {
        customer.setEmail("test1@fake.com");

        assertEquals("test1@fake.com", customer.getEmail());
    }

    @Test
    @DisplayName("set and get created at")
    public void shouldSetAndGetCreatedAt() {
        customer.setCreatedAt(testDateTime);

        assertEquals(testDateTime, customer.getCreatedAt());
    }

    @Test
    @DisplayName("customer with all fields set")
    public void shouldCreateCustomerWithAllFields() {
        customer.setId(1L);
        customer.setName("test");
        customer.setEmail("test@fake.com");
        customer.setCreatedAt(testDateTime);

        assertAll(
            () -> assertEquals(1L, customer.getId()),
            () -> assertEquals("test", customer.getName()),
            () -> assertEquals("test@fake.com", customer.getEmail()),
            () -> assertEquals(testDateTime, customer.getCreatedAt())
        );
    }

    @Test
    @DisplayName("customer toString method")
    public void shouldReturnCorrectToString() {
        customer.setId(1L);
        customer.setName("test");
        customer.setEmail("test@fake.com");
        customer.setCreatedAt(testDateTime);

        String expected = "Customer{" +
                "id=" + 1L +
                ", name='" + "test" + '\'' +
                ", email='" + "test@fake.com" + '\'' +
                ", createdAt=" + testDateTime +
                '}';

        assertEquals(expected, customer.toString());
    }

    @Test
    @DisplayName("customer name can be updated")
    public void shouldUpdateCustomerName() {
        customer.setName("test");
        assertEquals("test", customer.getName());

        customer.setName("test1");
        assertEquals("test1", customer.getName());
    }

    @Test
    @DisplayName("customer email can be updated")
    public void shouldUpdateCustomerEmail() {
        customer.setEmail("test@fake.com");
        assertEquals("test@fake.com", customer.getEmail());

        customer.setEmail("test1@fake.com");
        assertEquals("test1@fake.com", customer.getEmail());
    }

    @Test
    @DisplayName("customer with null values")
    public void shouldHandleNullValues() {
        customer.setName(null);
        customer.setEmail(null);
        customer.setCreatedAt(null);

        assertNull(customer.getName());
        assertNull(customer.getEmail());
        assertNull(customer.getCreatedAt());
    }

    @Test
    @DisplayName("customer id is independent")
    public void shouldHaveIndependentId() {
        Customer customer1 = new Customer("test", "test@fake.com");
        Customer customer2 = new Customer("test1", "test1@fake.com");

        customer1.setId(1L);
        customer2.setId(2L);

        assertEquals(1L, customer1.getId());
        assertEquals(2L, customer2.getId());
        assertNotEquals(customer1.getId(), customer2.getId());
    }

    @Test
    @DisplayName("multiple customers have independent data")
    public void shouldCreateMultipleIndependentCustomers() {
        Customer customer1 = new Customer("test", "test@fake.com");
        Customer customer2 = new Customer("test1", "test1@fake.com");

        customer1.setId(1L);
        customer2.setId(2L);
        customer1.setCreatedAt(testDateTime);
        customer2.setCreatedAt(testDateTime.plusDays(1));

        assertAll(
            () -> assertEquals("test", customer1.getName()),
            () -> assertEquals("test1", customer2.getName()),
            () -> assertEquals("test@fake.com", customer1.getEmail()),
            () -> assertEquals("test1@fake.com", customer2.getEmail()),
            () -> assertEquals(1L, customer1.getId()),
            () -> assertEquals(2L, customer2.getId()),
            () -> assertEquals(testDateTime, customer1.getCreatedAt()),
            () -> assertEquals(testDateTime.plusDays(1), customer2.getCreatedAt())
        );
    }

    @Test
    @DisplayName("customer name with special characters")
    public void shouldHandleNameWithSpecialCharacters() {
        String specialName = "test'Brien";
        customer.setName(specialName);

        assertEquals(specialName, customer.getName());
    }

    @Test
    @DisplayName("customer email with special characters")
    public void shouldHandleEmailWithSpecialCharacters() {
        String specialEmail = "test+tag@fake.co.uk";
        customer.setEmail(specialEmail);

        assertEquals(specialEmail, customer.getEmail());
    }

    @Test
    @DisplayName("customer with empty strings")
    public void shouldHandleEmptyStrings() {
        customer.setName("");
        customer.setEmail("");

        assertEquals("", customer.getName());
        assertEquals("", customer.getEmail());
    }

    @Test
    @DisplayName("customer with whitespace in name")
    public void shouldHandleWhitespaceInName() {
        String nameWithSpaces = "  test  ";
        customer.setName(nameWithSpaces);

        assertEquals(nameWithSpaces, customer.getName());
    }

    @Test
    @DisplayName("customer created at timestamp")
    public void shouldHandleCreatedAtTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        customer.setCreatedAt(now);

        assertNotNull(customer.getCreatedAt());
        assertTrue(customer.getCreatedAt().isEqual(now) || customer.getCreatedAt().isAfter(now.minusSeconds(1)));
    }

    @Test
    @DisplayName("customer with very long name")
    public void shouldHandleVeryLongName() {
        String longName = "test" + "a".repeat(251);
        customer.setName(longName);

        assertEquals(longName, customer.getName());
    }

    @Test
    @DisplayName("customer with very long email")
    public void shouldHandleVeryLongEmail() {
        String longEmail = "test" + "a".repeat(200) + "@fake.com";
        customer.setEmail(longEmail);

        assertEquals(longEmail, customer.getEmail());
    }

    @Test
    @DisplayName("customer id boundary values")
    public void shouldHandleIdBoundaryValues() {
        customer.setId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, customer.getId());

        customer.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, customer.getId());
    }

    @Test
    @DisplayName("customer constructors create independent instances")
    public void shouldCreateIndependentInstancesWithConstructors() {
        Customer customer1 = new Customer("test", "test@fake.com");
        Customer customer2 = new Customer("test1", "test1@fake.com");

        assertNotSame(customer1, customer2);
        assertNotEquals(customer1.getName(), customer2.getName());
        assertNotEquals(customer1.getEmail(), customer2.getEmail());
    }

    @Test
    @DisplayName("customer fields are independent")
    public void shouldHaveIndependentFields() {
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();

        customer1.setName("test");
        customer2.setName("test1");

        customer1.setEmail("test@fake.com");
        customer2.setEmail("test1@fake.com");

        assertNotEquals(customer1.getName(), customer2.getName());
        assertNotEquals(customer1.getEmail(), customer2.getEmail());
    }
}

