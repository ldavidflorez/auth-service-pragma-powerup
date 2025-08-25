package co.com.powerup.pragma.usecase.findbyemail;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindByEmailUseCase {
    
    private final UserRepository userRepository;
    
    /**
     * Finds a user by email address
     * @param email the email to search for
     * @return Mono<User> the user if found, empty Mono otherwise
     */
    public Mono<User> findByEmail(String email) {
        return Mono.defer(() -> {
            System.out.println("DEBUG: Validando formato de email para busqueda: " + email);
            validateEmail(email);
            System.out.println("INFO: Iniciando busqueda de usuario por email: " + email);
            System.out.println("DEBUG: Buscando usuario en repositorio con email: " + email);
            return userRepository.findByEmail(email)
                    .doOnSuccess(user -> {
                        if (user != null) {
                            System.out.println("INFO: Usuario encontrado exitosamente - ID: " + user.getId() + " y email: " + user.getCorreoElectronico());
                        } else {
                            System.out.println("INFO: Usuario no encontrado con email: " + email);
                        }
                    })
                    .doOnError(error -> System.out.println("ERROR: Error al buscar usuario con email " + email + ": " + error.getMessage()));
        });
    }
    
    /**
     * Validates the email format
     * @param email the email to validate
     */
    private void validateEmail(String email) {
        System.out.println("DEBUG: Validando formato de email para busqueda: " + email);
        if (email == null || email.trim().isEmpty()) {
            System.out.println("ERROR: Validacion de email fallida para busqueda: email nulo o vacio");
            throw new IllegalArgumentException("El email no puede ser nulo o vacio");
        }
        if (!email.contains("@")) {
            System.out.println("ERROR: Validacion de email fallida para busqueda: formato invalido - " + email);
            throw new IllegalArgumentException("El email debe tener un formato valido");
        }
        System.out.println("DEBUG: Validacion de email exitosa para busqueda: " + email);
    }
}
