package co.com.powerup.pragma.r2dbc;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.model.user.gateways.UserRepository;
import co.com.powerup.pragma.r2dbc.data.UserData;
import co.com.powerup.pragma.r2dbc.helper.ReactiveAdapterOperations;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserData,
    Long,
    UserReactiveRepository
> implements UserRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);
    
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, User.UserBuilder.class).build());
    }
    
    @Override
    @Transactional
    public Mono<User> save(User user) {
        logger.info("Saving user to database - ID: {} and email: {}", user.getId(), user.getEmail());
        return super.save(user)
                .doOnSuccess(savedUser -> logger.info("User saved successfully in DB - ID: {} and email: {}", 
                        savedUser.getId(), savedUser.getEmail()))
                .doOnError(error -> logger.error("Error saving user in DB - email {}: {}", 
                        user.getEmail(), error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        logger.debug("Checking user existence in DB with email: {}", email);
        return repository.existsByEmail(email)
                .doOnSuccess(exists -> logger.debug("Existence check result - email: {} exists: {}", email, exists))
                .doOnError(error -> logger.error("Error checking user existence in DB - email {}: {}", email, error.getMessage()));
    }
    
    @Override
    protected UserData toData(User user) {
        return UserData.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .phone(user.getPhone())
                .email(user.getEmail())
                .baseSalary(user.getBaseSalary())
                .registrationDate(user.getRegistrationDate())
                .build();
    }
    
    @Override
    protected User toEntity(UserData userData) {
        return User.builder()
                .id(userData.getId())
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .dateOfBirth(userData.getDateOfBirth())
                .address(userData.getAddress())
                .phone(userData.getPhone())
                .email(userData.getEmail())
                .baseSalary(userData.getBaseSalary())
                .registrationDate(userData.getRegistrationDate())
                .build();
    }
}
