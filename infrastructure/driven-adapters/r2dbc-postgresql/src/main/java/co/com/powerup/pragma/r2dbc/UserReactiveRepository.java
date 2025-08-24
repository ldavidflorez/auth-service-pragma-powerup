package co.com.powerup.pragma.r2dbc;

import co.com.powerup.pragma.r2dbc.data.UserData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserData, Long>, ReactiveQueryByExampleExecutor<UserData> {
    
    /**
     * Finds a user by email address
     * @param correoElectronico the email to search for
     * @return Mono<UserData> the user if found, empty Mono otherwise
     */
    Mono<UserData> findByCorreoElectronico(String correoElectronico);
    
    /**
     * Checks if a user exists with the given email address
     * @param correoElectronico the email to check
     * @return Mono<Boolean> true if user exists, false otherwise
     */
    Mono<Boolean> existsByCorreoElectronico(String correoElectronico);
    
    /**
     * Retrieves all users from the database
     * @return Flux<UserData> all users in the system
     */
    Flux<UserData> findAll();
}
