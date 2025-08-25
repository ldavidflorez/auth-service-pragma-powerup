package co.com.powerup.pragma.usecase.findbyemail;

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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindByEmailUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private FindByEmailUseCase findByEmailUseCase;

    @BeforeEach
    void setUp() {
        findByEmailUseCase = new FindByEmailUseCase(userRepository);
    }

    @Test
    void findByEmail_Success() {
        // Arrange
        String email = "juan.perez@test.com";
        User expectedUser = User.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .correoElectronico(email)
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .salarioBase(5000000.0)
                .fechaRegistro(LocalDate.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(expectedUser));

        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail(email))
                .expectNextMatches(user -> 
                    user.getId().equals(1L) &&
                    user.getCorreoElectronico().equals(email) &&
                    user.getNombres().equals("Juan") &&
                    user.getApellidos().equals("Perez")
                )
                .verifyComplete();
    }

    @Test
    void findByEmail_UserNotFound() {
        // Arrange
        String email = "nonexistent@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail(email))
                .verifyComplete();
    }

    @Test
    void findByEmail_NullEmail() {
        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByEmail_EmptyEmail() {
        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail(""))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByEmail_BlankEmail() {
        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail("   "))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByEmail_InvalidEmailFormat() {
        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail("invalid-email"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByEmail_EmailWithoutAtSymbol() {
        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail("testemail.com"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByEmail_RepositoryError() {
        // Arrange
        String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail(email))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void findByEmail_WithSpecialCharacters() {
        // Arrange
        String email = "test+tag@domain.com";
        User expectedUser = User.builder()
                .id(2L)
                .nombres("Test")
                .apellidos("User")
                .correoElectronico(email)
                .fechaNacimiento(LocalDate.of(1985, 10, 20))
                .salarioBase(6000000.0)
                .fechaRegistro(LocalDate.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(expectedUser));

        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail(email))
                .expectNextMatches(user -> 
                    user.getId().equals(2L) &&
                    user.getCorreoElectronico().equals(email)
                )
                .verifyComplete();
    }

    @Test
    void findByEmail_CaseSensitive() {
        // Arrange
        String email = "TEST@DOMAIN.COM";
        User expectedUser = User.builder()
                .id(3L)
                .nombres("Test")
                .apellidos("User")
                .correoElectronico(email)
                .fechaNacimiento(LocalDate.of(1992, 3, 15))
                .salarioBase(4500000.0)
                .fechaRegistro(LocalDate.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(expectedUser));

        // Act & Assert
        StepVerifier.create(findByEmailUseCase.findByEmail(email))
                .expectNextMatches(user -> 
                    user.getId().equals(3L) &&
                    user.getCorreoElectronico().equals(email)
                )
                .verifyComplete();
    }
}
