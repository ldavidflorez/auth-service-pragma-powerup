package co.com.powerup.pragma.usecase.listallusers;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ListAllUsersUseCase {
    
    private final UserRepository userRepository;
    
    /**
     * Retrieves all users from the database
     * @return Flux<User> all users in the system
     */
    public Flux<User> listAllUsers() {
        System.out.println("INFO: Iniciando listado de todos los usuarios");
        return userRepository.findAll()
                .doOnNext(user -> System.out.println("DEBUG: Usuario encontrado - ID: " + user.getId() + " y email: " + user.getCorreoElectronico()))
                .doOnComplete(() -> System.out.println("INFO: Listado de usuarios completado"))
                .doOnError(error -> System.out.println("ERROR: Error al listar usuarios: " + error.getMessage()));
    }
}
