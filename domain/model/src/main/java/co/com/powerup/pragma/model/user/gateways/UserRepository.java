package co.com.powerup.pragma.model.user.gateways;

import co.com.powerup.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    
    /**
     * Saves a new user to the database
     * @param user the user to save
     * @return Mono<User> the saved user with generated ID
     */
    Mono<User> save(User user);
    
    /**
     * Checks if an email is already registered in the system
     * @param email the email to check
     * @return Mono<Boolean> true if email exists, false otherwise
     */
    Mono<Boolean> existsByEmail(String email);
    
    /**
     * Finds a user by email
     * @param email the email to search for
     * @return Mono<User> the user if found, empty Mono otherwise
     */
    Mono<User> findByEmail(String email);
}
