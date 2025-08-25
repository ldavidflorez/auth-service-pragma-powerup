package co.com.powerup.pragma.usecase.listallusers;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAllUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private ListAllUsersUseCase listAllUsersUseCase;

    @BeforeEach
    void setUp() {
        listAllUsersUseCase = new ListAllUsersUseCase(userRepository);
    }

    @Test
    void listAllUsers_Success() {
        // Arrange
        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .nombres("Juan")
                        .apellidos("Pérez")
                        .correoElectronico("juan.perez@test.com")
                        .fechaNacimiento(LocalDate.of(1990, 5, 15))
                        .salarioBase(5000000.0)
                        .fechaRegistro(LocalDate.now())
                        .build(),
                User.builder()
                        .id(2L)
                        .nombres("María")
                        .apellidos("García")
                        .correoElectronico("maria.garcia@test.com")
                        .fechaNacimiento(LocalDate.of(1985, 8, 20))
                        .salarioBase(6000000.0)
                        .fechaRegistro(LocalDate.now())
                        .build(),
                User.builder()
                        .id(3L)
                        .nombres("Carlos")
                        .apellidos("López")
                        .correoElectronico("carlos.lopez@test.com")
                        .fechaNacimiento(LocalDate.of(1992, 3, 10))
                        .salarioBase(4500000.0)
                        .fechaRegistro(LocalDate.now())
                        .build()
        );

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        // Act & Assert
        StepVerifier.create(listAllUsersUseCase.listAllUsers())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void listAllUsers_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(listAllUsersUseCase.listAllUsers())
                .verifyComplete();
    }

    @Test
    void listAllUsers_SingleUser() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .nombres("Ana")
                .apellidos("Rodríguez")
                .correoElectronico("ana.rodriguez@test.com")
                .fechaNacimiento(LocalDate.of(1988, 12, 25))
                .salarioBase(5500000.0)
                .fechaRegistro(LocalDate.now())
                .build();

        when(userRepository.findAll()).thenReturn(Flux.just(user));

        // Act & Assert
        StepVerifier.create(listAllUsersUseCase.listAllUsers())
                .expectNextMatches(result -> 
                    result.getId().equals(1L) &&
                    result.getNombres().equals("Ana") &&
                    result.getApellidos().equals("Rodríguez") &&
                    result.getCorreoElectronico().equals("ana.rodriguez@test.com")
                )
                .verifyComplete();
    }

    @Test
    void listAllUsers_RepositoryError() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(listAllUsersUseCase.listAllUsers())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void listAllUsers_LargeDataset() {
        // Arrange
        List<User> users = List.of(
                User.builder().id(1L).nombres("User1").apellidos("Test1").correoElectronico("user1@test.com").fechaNacimiento(LocalDate.of(1990, 1, 1)).salarioBase(1000000.0).fechaRegistro(LocalDate.now()).build(),
                User.builder().id(2L).nombres("User2").apellidos("Test2").correoElectronico("user2@test.com").fechaNacimiento(LocalDate.of(1991, 2, 2)).salarioBase(2000000.0).fechaRegistro(LocalDate.now()).build(),
                User.builder().id(3L).nombres("User3").apellidos("Test3").correoElectronico("user3@test.com").fechaNacimiento(LocalDate.of(1992, 3, 3)).salarioBase(3000000.0).fechaRegistro(LocalDate.now()).build(),
                User.builder().id(4L).nombres("User4").apellidos("Test4").correoElectronico("user4@test.com").fechaNacimiento(LocalDate.of(1993, 4, 4)).salarioBase(4000000.0).fechaRegistro(LocalDate.now()).build(),
                User.builder().id(5L).nombres("User5").apellidos("Test5").correoElectronico("user5@test.com").fechaNacimiento(LocalDate.of(1994, 5, 5)).salarioBase(5000000.0).fechaRegistro(LocalDate.now()).build()
        );

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        // Act & Assert
        StepVerifier.create(listAllUsersUseCase.listAllUsers())
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void listAllUsers_VerifyOrder() {
        // Arrange
        List<User> users = List.of(
                User.builder().id(1L).nombres("First").apellidos("User").correoElectronico("first@test.com").fechaNacimiento(LocalDate.of(1990, 1, 1)).salarioBase(1000000.0).fechaRegistro(LocalDate.now()).build(),
                User.builder().id(2L).nombres("Second").apellidos("User").correoElectronico("second@test.com").fechaNacimiento(LocalDate.of(1991, 2, 2)).salarioBase(2000000.0).fechaRegistro(LocalDate.now()).build()
        );

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        // Act & Assert
        StepVerifier.create(listAllUsersUseCase.listAllUsers())
                .expectNextMatches(user -> user.getId().equals(1L) && user.getNombres().equals("First"))
                .expectNextMatches(user -> user.getId().equals(2L) && user.getNombres().equals("Second"))
                .verifyComplete();
    }

    @Test
    void listAllUsers_WithNullValues() {
        // Arrange
        List<User> users = List.of(
                User.builder().id(1L).nombres("User").apellidos("Test").correoElectronico("user@test.com").fechaNacimiento(LocalDate.of(1990, 1, 1)).salarioBase(1000000.0).fechaRegistro(LocalDate.now()).build(),
                User.builder().id(2L).nombres("User2").apellidos("Test2").correoElectronico("user2@test.com").fechaNacimiento(LocalDate.of(1991, 2, 2)).salarioBase(2000000.0).fechaRegistro(LocalDate.now()).telefono(null).direccion(null).build()
        );

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        // Act & Assert
        StepVerifier.create(listAllUsersUseCase.listAllUsers())
                .expectNextCount(2)
                .verifyComplete();
    }
}
