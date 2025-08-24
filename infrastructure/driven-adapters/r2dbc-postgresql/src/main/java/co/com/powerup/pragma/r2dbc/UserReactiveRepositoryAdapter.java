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
        logger.info("Guardando usuario en base de datos - ID: {} y email: {}", user.getId(), user.getCorreoElectronico());
        UserData userData = toData(user);
        return repository.save(userData)
                .doOnSuccess(savedData -> logger.info("Usuario guardado exitosamente en BD - ID: {} y email: {}", 
                        savedData.getId(), savedData.getCorreoElectronico()))
                .doOnError(error -> logger.error("Error al guardar usuario en BD - email {}: {}", 
                        user.getCorreoElectronico(), error.getMessage()))
                .map(this::toEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existsByEmail(String email) {
        logger.debug("Verificando existencia de usuario en BD con email: {}", email);
        return repository.existsByCorreoElectronico(email)
                .doOnSuccess(exists -> logger.debug("Resultado de verificacion de existencia - email: {} existe: {}", email, exists))
                .doOnError(error -> logger.error("Error al verificar existencia de usuario en BD - email {}: {}", email, error.getMessage()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Mono<User> findByEmail(String email) {
        logger.debug("Buscando usuario en BD con email: {}", email);
        return repository.findByCorreoElectronico(email)
                .doOnSuccess(userData -> {
                    if (userData != null) {
                        logger.debug("Usuario encontrado en BD - ID: {} y email: {}", userData.getId(), userData.getCorreoElectronico());
                    } else {
                        logger.debug("Usuario no encontrado en BD con email: {}", email);
                    }
                })
                .doOnError(error -> logger.error("Error al buscar usuario en BD - email {}: {}", email, error.getMessage()))
                .map(this::toEntity);
    }
    
    @Override
    protected UserData toData(User user) {
        return UserData.builder()
                .id(user.getId())
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .correoElectronico(user.getCorreoElectronico())
                .salarioBase(user.getSalarioBase())
                .fechaRegistro(user.getFechaRegistro())
                .build();
    }
    
    @Override
    protected User toEntity(UserData userData) {
        return User.builder()
                .id(userData.getId())
                .nombres(userData.getNombres())
                .apellidos(userData.getApellidos())
                .fechaNacimiento(userData.getFechaNacimiento())
                .direccion(userData.getDireccion())
                .telefono(userData.getTelefono())
                .correoElectronico(userData.getCorreoElectronico())
                .salarioBase(userData.getSalarioBase())
                .fechaRegistro(userData.getFechaRegistro())
                .build();
    }
}
