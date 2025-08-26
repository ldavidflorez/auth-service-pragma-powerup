package co.com.powerup.pragma.r2dbc;

import co.com.powerup.pragma.r2dbc.data.UserData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserData, Long>, ReactiveQueryByExampleExecutor<UserData> {
    
    /**
     * Checks if a user exists with the given email address
     * @param email the email to check
     * @return Mono<Boolean> true if user exists, false otherwise
     */
    Mono<Boolean> existsByEmail(String email);
}
