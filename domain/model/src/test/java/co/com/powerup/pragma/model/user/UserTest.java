package co.com.powerup.pragma.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Test
    void testUserBuilder() {
        // Arrange & Act
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .phone("3001234567")
                .address("123 Main St")
                .registrationDate(LocalDate.now())
                .build();

        // Assert
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@test.com", user.getEmail());
        assertEquals(LocalDate.of(1990, 5, 15), user.getDateOfBirth());
        assertEquals(new BigDecimal("5000000.00"), user.getBaseSalary());
        assertEquals("3001234567", user.getPhone());
        assertEquals("123 Main St", user.getAddress());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    void testUserSettersAndGetters() {
        // Arrange
        User user = new User();

        // Act
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@test.com");
        user.setDateOfBirth(LocalDate.of(1985, 8, 20));
        user.setBaseSalary(new BigDecimal("6000000.00"));
        user.setPhone("3109876543");
        user.setAddress("456 Oak Ave");
        user.setRegistrationDate(LocalDate.now());

        // Assert
        assertEquals(2L, user.getId());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane.smith@test.com", user.getEmail());
        assertEquals(LocalDate.of(1985, 8, 20), user.getDateOfBirth());
        assertEquals(new BigDecimal("6000000.00"), user.getBaseSalary());
        assertEquals("3109876543", user.getPhone());
        assertEquals("456 Oak Ave", user.getAddress());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    void testSetRegistrationDate() {
        // Arrange
        User user = new User();
        LocalDate beforeCall = LocalDate.now();

        // Act
        user.setRegistrationDate();

        // Assert
        assertNotNull(user.getRegistrationDate());
        assertTrue(user.getRegistrationDate().isAfter(beforeCall.minusDays(1)) || 
                  user.getRegistrationDate().isEqual(beforeCall));
    }

    @Test
    void testUserWithNullValues() {
        // Arrange & Act
        User user = User.builder()
                .id(3L)
                .firstName("Carlos")
                .lastName("Lopez")
                .email("carlos.lopez@test.com")
                .dateOfBirth(LocalDate.of(1992, 3, 10))
                .baseSalary(new BigDecimal("4500000.00"))
                .build();

        // Assert
        assertNotNull(user);
        assertEquals(3L, user.getId());
        assertEquals("Carlos", user.getFirstName());
        assertEquals("Lopez", user.getLastName());
        assertEquals("carlos.lopez@test.com", user.getEmail());
        assertEquals(LocalDate.of(1992, 3, 10), user.getDateOfBirth());
        assertEquals(new BigDecimal("4500000.00"), user.getBaseSalary());
        assertNull(user.getPhone());
        assertNull(user.getAddress());
        assertNull(user.getRegistrationDate());
    }

    @Test
    void testUserToBuilder() {
        // Arrange
        User originalUser = User.builder()
                .id(4L)
                .firstName("Original")
                .lastName("User")
                .email("original@test.com")
                .dateOfBirth(LocalDate.of(1995, 7, 14))
                .baseSalary(new BigDecimal("4000000.00"))
                .phone("3205551234")
                .address("789 Pine Rd")
                .registrationDate(LocalDate.now())
                .build();

        // Act
        User modifiedUser = originalUser.toBuilder()
                .firstName("Modified")
                .email("modified@test.com")
                .build();

        // Assert
        assertNotEquals(originalUser.getFirstName(), modifiedUser.getFirstName());
        assertNotEquals(originalUser.getEmail(), modifiedUser.getEmail());
        assertEquals(originalUser.getLastName(), modifiedUser.getLastName());
        assertEquals(originalUser.getDateOfBirth(), modifiedUser.getDateOfBirth());
        assertEquals(originalUser.getBaseSalary(), modifiedUser.getBaseSalary());
    }

    @Test
    void testUserEquals() {
        // Arrange
        User user1 = User.builder()
                .id(5L)
                .firstName("Test")
                .lastName("User")
                .email("test@test.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        User user2 = User.builder()
                .id(5L)
                .firstName("Test")
                .lastName("User")
                .email("test@test.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Assert
        assertEquals(user1.getFirstName(), user2.getFirstName());
        assertEquals(user1.getLastName(), user2.getLastName());
        assertEquals(user1.getEmail(), user2.getEmail());
        assertEquals(user1.getDateOfBirth(), user2.getDateOfBirth());
        assertEquals(user1.getBaseSalary(), user2.getBaseSalary());
    }

    @Test
    void testUserWithSpecialCharacters() {
        // Arrange & Act
        User user = User.builder()
                .id(6L)
                .firstName("José María")
                .lastName("García-López")
                .email("jose.maria+tag@domain.co.uk")
                .dateOfBirth(LocalDate.of(1988, 12, 25))
                .baseSalary(new BigDecimal("5500000.00"))
                .phone("+573001234567")
                .address("Calle 123 #45-67, Apto 101")
                .registrationDate(LocalDate.now())
                .build();

        // Assert
        assertEquals("José María", user.getFirstName());
        assertEquals("García-López", user.getLastName());
        assertEquals("jose.maria+tag@domain.co.uk", user.getEmail());
        assertEquals("+573001234567", user.getPhone());
        assertEquals("Calle 123 #45-67, Apto 101", user.getAddress());
    }
}
