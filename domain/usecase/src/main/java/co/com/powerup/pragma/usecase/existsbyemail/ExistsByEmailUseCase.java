package co.com.powerup.pragma.usecase.existsbyemail;

import co.com.powerup.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ExistsByEmailUseCase {
    
    private final UserRepository userRepository;
    
    /**
     * Checks if a user exists with the given email address
     * @param email the email to check
     * @return Mono<Boolean> true if user exists, false otherwise
     */
    public Mono<Boolean> existsByEmail(String email) {
        System.out.println("INFO: Verificando existencia de usuario con email: " + email);
        return Mono.just(email)
                .doOnNext(e -> System.out.println("DEBUG: Validando formato de email para verificacion: " + e))
                .doOnNext(this::validateEmail)
                .flatMap(e -> {
                    System.out.println("DEBUG: Verificando existencia en repositorio con email: " + e);
                    return userRepository.existsByEmail(e);
                })
                .doOnSuccess(exists -> {
                    if (exists) {
                        System.out.println("INFO: Usuario existe con email: " + email);
                    } else {
                        System.out.println("INFO: Usuario no existe con email: " + email);
                    }
                })
                .doOnError(error -> System.out.println("ERROR: Error al verificar existencia de usuario con email " + email + ": " + error.getMessage()));
    }
    
    /**
     * Validates the email format
     * @param email the email to validate
     */
    private void validateEmail(String email) {
        System.out.println("DEBUG: Validando formato de email para verificacion de existencia: " + email);
        if (email == null || email.trim().isEmpty()) {
            System.out.println("ERROR: Validacion de email fallida para verificacion: email nulo o vacio");
            throw new IllegalArgumentException("El email no puede ser nulo o vacio");
        }
        if (!email.contains("@")) {
            System.out.println("ERROR: Validacion de email fallida para verificacion: formato invalido - " + email);
            throw new IllegalArgumentException("El email debe tener un formato valido");
        }
        System.out.println("DEBUG: Validacion de email exitosa para verificacion: " + email);
    }
}
