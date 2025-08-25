package co.com.powerup.pragma.usecase.existsbyemail;

import co.com.powerup.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistsByEmailUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private ExistsByEmailUseCase existsByEmailUseCase;

    @BeforeEach
    void setUp() {
        existsByEmailUseCase = new ExistsByEmailUseCase(userRepository);
    }

    @Test
    void existsByEmail_UserExists() {
        // Arrange
        String email = "juan.perez@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(email))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmail_UserDoesNotExist() {
        // Arrange
        String email = "nonexistent@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByEmail_NullEmail() {
        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void existsByEmail_EmptyEmail() {
        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(""))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void existsByEmail_BlankEmail() {
        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail("   "))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void existsByEmail_InvalidEmailFormat() {
        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail("invalid-email"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void existsByEmail_EmailWithoutAtSymbol() {
        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail("testemail.com"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void existsByEmail_RepositoryError() {
        // Arrange
        String email = "test@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(email))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void existsByEmail_WithSpecialCharacters() {
        // Arrange
        String email = "test+tag@domain.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(email))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmail_CaseSensitive() {
        // Arrange
        String email = "TEST@DOMAIN.COM";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByEmail_ComplexEmail() {
        // Arrange
        String email = "user.name+tag@subdomain.example.co.uk";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(email))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmail_EmailWithNumbers() {
        // Arrange
        String email = "user123@domain123.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(existsByEmailUseCase.existsByEmail(email))
                .expectNext(false)
                .verifyComplete();
    }
}
