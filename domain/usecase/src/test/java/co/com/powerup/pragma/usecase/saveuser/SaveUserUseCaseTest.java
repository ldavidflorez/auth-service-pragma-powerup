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
                .nombres("Juan")
                .apellidos("Perez")
                .correoElectronico("juan.perez@test.com")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .salarioBase(5000000.0)
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .build();

        User savedUser = user.toBuilder()
                .id(1L)
                .fechaRegistro(LocalDate.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectNextMatches(result -> 
                    result.getId().equals(1L) &&
                    result.getCorreoElectronico().equals("juan.perez@test.com") &&
                    result.getFechaRegistro() != null
                )
                .verifyComplete();
    }

    @Test
    void saveUserWithEmailValidation_Success() {
        // Arrange
        User user = User.builder()
                .nombres("Maria")
                .apellidos("Garcia")
                .correoElectronico("maria.garcia@test.com")
                .fechaNacimiento(LocalDate.of(1985, 8, 20))
                .salarioBase(6000000.0)
                .telefono("3109876543")
                .direccion("Avenida 456 #78-90")
                .build();

        User savedUser = user.toBuilder()
                .id(2L)
                .fechaRegistro(LocalDate.now())
                .build();

        when(userRepository.existsByEmail("maria.garcia@test.com")).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUserWithEmailValidation(user))
                .expectNextMatches(result -> 
                    result.getId().equals(2L) &&
                    result.getCorreoElectronico().equals("maria.garcia@test.com") &&
                    result.getFechaRegistro() != null
                )
                .verifyComplete();
    }

    @Test
    void saveUserWithEmailValidation_EmailAlreadyExists() {
        // Arrange
        User user = User.builder()
                .nombres("Carlos")
                .apellidos("Lopez")
                .correoElectronico("carlos.lopez@test.com")
                .fechaNacimiento(LocalDate.of(1992, 3, 10))
                .salarioBase(4500000.0)
                .build();

        when(userRepository.existsByEmail("carlos.lopez@test.com")).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUserWithEmailValidation(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_InvalidEmail() {
        // Arrange
        User user = User.builder()
                .nombres("Ana")
                .apellidos("Rodriguez")
                .correoElectronico("invalid-email")
                .fechaNacimiento(LocalDate.of(1988, 12, 25))
                .salarioBase(5500000.0)
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_NullNames() {
        // Arrange
        User user = User.builder()
                .nombres(null)
                .apellidos("Martinez")
                .correoElectronico("test@test.com")
                .fechaNacimiento(LocalDate.of(1995, 7, 14))
                .salarioBase(4000000.0)
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_EmptyNames() {
        // Arrange
        User user = User.builder()
                .nombres("")
                .apellidos("Gonzalez")
                .correoElectronico("test@test.com")
                .fechaNacimiento(LocalDate.of(1993, 9, 30))
                .salarioBase(4800000.0)
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_InvalidSalary() {
        // Arrange
        User user = User.builder()
                .nombres("Pedro")
                .apellidos("Hernandez")
                .correoElectronico("pedro@test.com")
                .fechaNacimiento(LocalDate.of(1991, 4, 18))
                .salarioBase(-1000000.0)
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_SalaryTooHigh() {
        // Arrange
        User user = User.builder()
                .nombres("Laura")
                .apellidos("Diaz")
                .correoElectronico("laura@test.com")
                .fechaNacimiento(LocalDate.of(1987, 11, 5))
                .salarioBase(20000000.0)
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_FutureDateOfBirth() {
        // Arrange
        User user = User.builder()
                .nombres("Roberto")
                .apellidos("Moreno")
                .correoElectronico("roberto@test.com")
                .fechaNacimiento(LocalDate.now().plusDays(1))
                .salarioBase(5200000.0)
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_InvalidPhone() {
        // Arrange
        User user = User.builder()
                .nombres("Carmen")
                .apellidos("Vargas")
                .correoElectronico("carmen@test.com")
                .fechaNacimiento(LocalDate.of(1989, 6, 22))
                .salarioBase(4700000.0)
                .telefono("abc123")
                .build();

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void saveUser_RepositoryError() {
        // Arrange
        User user = User.builder()
                .nombres("Fernando")
                .apellidos("Silva")
                .correoElectronico("fernando@test.com")
                .fechaNacimiento(LocalDate.of(1994, 2, 8))
                .salarioBase(5100000.0)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectError(RuntimeException.class)
                .verify();
    }
}
