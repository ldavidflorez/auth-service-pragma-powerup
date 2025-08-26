package co.com.powerup.pragma.usecase.saveuser;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private SaveUserUseCase saveUserUseCase;

    @BeforeEach
    void setUp() {
        saveUserUseCase = new SaveUserUseCase(userRepository);
    }

    @Test
    void saveUser_Success() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .phone("3001234567")
                .address("123 Main St")
                .build();

        User savedUser = user.toBuilder()
                .id(1L)
                .registrationDate(LocalDate.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectNextMatches(result -> 
                    result.getId().equals(1L) &&
                    result.getEmail().equals("john.doe@test.com") &&
                    result.getRegistrationDate() != null
                )
                .verifyComplete();
    }

    @Test
    void saveUserWithEmailValidation_Success() {
        // Arrange
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@test.com")
                .dateOfBirth(LocalDate.of(1985, 8, 20))
                .baseSalary(new BigDecimal("6000000.00"))
                .phone("3109876543")
                .address("456 Oak Ave")
                .build();

        User savedUser = user.toBuilder()
                .id(2L)
                .registrationDate(LocalDate.now())
                .build();

        when(userRepository.existsByEmail("jane.smith@test.com")).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUserWithEmailValidation(user))
                .expectNextMatches(result -> 
                    result.getId().equals(2L) &&
                    result.getEmail().equals("jane.smith@test.com") &&
                    result.getRegistrationDate() != null
                )
                .verifyComplete();
    }

    @Test
    void saveUserWithEmailValidation_EmailAlreadyExists() {
        // Arrange
        User user = User.builder()
                .firstName("Carlos")
                .lastName("Lopez")
                .email("carlos.lopez@test.com")
                .dateOfBirth(LocalDate.of(1992, 3, 10))
                .baseSalary(new BigDecimal("4500000.00"))
                .phone("3205551234")
                .address("789 Pine Rd")
                .build();

        when(userRepository.existsByEmail("carlos.lopez@test.com")).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUserWithEmailValidation(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Email already exists: carlos.lopez@test.com")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_NullFirstName() {
        // Arrange
        User user = User.builder()
                .firstName(null)
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("First name cannot be null or empty")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_EmptyLastName() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Last name cannot be null or empty")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_InvalidEmail() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("invalid-email")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Email must have a valid format")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_NullEmail() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(null)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Email cannot be null or empty")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_NegativeSalary() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("-1000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Base salary cannot be negative")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_ExcessiveSalary() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("20000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Base salary cannot exceed 15,000,000")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_FutureDateOfBirth() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.now().plusYears(1))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Date of birth cannot be in the future")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_InvalidDateOfBirth() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.now().minusYears(150))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Date of birth seems invalid")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_InvalidPhoneNumber() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .phone("abc123")
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Phone number must contain only digits")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_ShortPhoneNumber() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .phone("123")
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Phone number must contain only digits")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_LongAddress() {
        // Arrange
        String longAddress = "A".repeat(201);
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .address(longAddress)
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Address cannot exceed 200 characters")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_ShortFirstName() {
        // Arrange
        User user = User.builder()
                .firstName("J")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("First name must have at least 2 characters")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_LongFirstName() {
        // Arrange
        String longName = "A".repeat(51);
        User user = User.builder()
                .firstName(longName)
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("First name cannot exceed 50 characters")
                )
                .verify();
    }

    @Test
    void saveUser_ValidationError_FirstNameWithNumbers() {
        // Arrange
        User user = User.builder()
                .firstName("John123")
                .lastName("Doe")
                .email("john.doe@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("5000000.00"))
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("First name can only contain letters and spaces")
                )
                .verify();
    }
}
