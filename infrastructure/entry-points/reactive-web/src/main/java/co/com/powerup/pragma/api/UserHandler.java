package co.com.powerup.pragma.api;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.usecase.saveuser.SaveUserUseCase;
import co.com.powerup.pragma.usecase.findbyemail.FindByEmailUseCase;
import co.com.powerup.pragma.usecase.listallusers.ListAllUsersUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private final SaveUserUseCase saveUserUseCase;
    private final FindByEmailUseCase findByEmailUseCase;
    private final ListAllUsersUseCase listAllUsersUseCase;
    
    /**
     * Handles the POST request for user registration
     * @param serverRequest the incoming request
     * @return ServerResponse with the created user or error
     */
    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        logger.info("Recibida solicitud de registro de usuario");
        return serverRequest.bodyToMono(User.class)
                .doOnNext(user -> logger.info("Procesando registro de usuario con email: {}", user.getCorreoElectronico()))
                .flatMap(saveUserUseCase::saveUserWithEmailValidation)
                .flatMap(user -> {
                    logger.info("Usuario registrado exitosamente - ID: {} y email: {}", user.getId(), user.getCorreoElectronico());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .bodyValue(UserResponse.builder()
                                    .id(user.getId())
                                    .nombres(user.getNombres())
                                    .apellidos(user.getApellidos())
                                    .correoElectronico(user.getCorreoElectronico())
                                    .fechaRegistro(user.getFechaRegistro() != null ? user.getFechaRegistro().toString() : null)
                                    .mensaje("Usuario registrado exitosamente")
                                    .build());
                })
                .onErrorResume(IllegalArgumentException.class, error -> {
                    logger.warn("Error de validacion en registro de usuario: {}", error.getMessage());
                    return ServerResponse.badRequest()
                            .bodyValue(ErrorResponse.builder()
                                    .mensaje("Error de validacion: " + error.getMessage())
                                    .codigo("VALIDATION_ERROR")
                                    .build());
                })
                .onErrorResume(Throwable.class, error -> {
                    logger.error("Error interno en registro de usuario: {}", error.getMessage(), error);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(ErrorResponse.builder()
                                    .mensaje("Error interno del servidor: " + error.getMessage())
                                    .codigo("INTERNAL_ERROR")
                                    .build());
                });
    }
    
    /**
     * Handles the GET request for finding a user by email
     * @param serverRequest the incoming request with email as query parameter
     * @return ServerResponse with the found user or error
     */
    public Mono<ServerResponse> findUserByEmail(ServerRequest serverRequest) {
        logger.info("Recibida solicitud de busqueda de usuario por email");
        return Mono.justOrEmpty(serverRequest.queryParam("email"))
                .doOnNext(email -> logger.info("Buscando usuario con email: {}", email))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El parametro email es requerido")))
                .flatMap(findByEmailUseCase::findByEmail)
                .flatMap(user -> {
                    logger.info("Usuario encontrado exitosamente - ID: {} y email: {}", user.getId(), user.getCorreoElectronico());
                    return ServerResponse.ok()
                            .bodyValue(UserResponse.builder()
                                    .id(user.getId())
                                    .nombres(user.getNombres())
                                    .apellidos(user.getApellidos())
                                    .correoElectronico(user.getCorreoElectronico())
                                    .fechaRegistro(user.getFechaRegistro() != null ? user.getFechaRegistro().toString() : null)
                                    .mensaje("Usuario encontrado exitosamente")
                                    .build());
                })
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(IllegalArgumentException.class, error -> {
                    logger.warn("Error de validacion en busqueda de usuario: {}", error.getMessage());
                    return ServerResponse.badRequest()
                            .bodyValue(ErrorResponse.builder()
                                    .mensaje("Error de validacion: " + error.getMessage())
                                    .codigo("VALIDATION_ERROR")
                                    .build());
                })
                .onErrorResume(Throwable.class, error -> {
                    logger.error("Error interno en busqueda de usuario: {}", error.getMessage(), error);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(ErrorResponse.builder()
                                    .mensaje("Error interno del servidor: " + error.getMessage())
                                    .codigo("INTERNAL_ERROR")
                                    .build());
                });
    }
    
    /**
     * Handles the GET request for listing all users
     * @param serverRequest the incoming request
     * @return ServerResponse with all users or error
     */
    public Mono<ServerResponse> listAllUsers(ServerRequest serverRequest) {
        logger.info("Recibida solicitud de listado de todos los usuarios");
        return ServerResponse.ok()
                // .contentType(MediaType.APPLICATION_JSON)
                // .contentType(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(
                    listAllUsersUseCase.listAllUsers()
                        .delayElements(Duration.ofMillis(1000)) // Delay para visualizar el flujo
                        .doOnNext(user -> logger.info("Procesando usuario - ID: {} y email: {}", user.getId(), user.getCorreoElectronico()))
                        .map(user -> UserResponse.builder()
                                .id(user.getId())
                                .nombres(user.getNombres())
                                .apellidos(user.getApellidos())
                                .correoElectronico(user.getCorreoElectronico())
                                .fechaRegistro(user.getFechaRegistro() != null ? user.getFechaRegistro().toString() : null)
                                .build())
                        .doOnNext(userResponse -> logger.info("Usuario mapeado - ID: {} y email: {}", userResponse.getId(), userResponse.getCorreoElectronico())),
                    UserResponse.class
                )
                .onErrorResume(Throwable.class, error -> {
                    logger.error("Error interno en listado de usuarios: {}", error.getMessage(), error);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ErrorResponse.builder()
                                    .mensaje("Error interno del servidor: " + error.getMessage())
                                    .codigo("INTERNAL_ERROR")
                                    .build());
                });
    }
    
    /**
     * Response DTO for user registration
     */
    public static class UserResponse {
        private Long id;
        private String nombres;
        private String apellidos;
        private String correoElectronico;
        private String fechaRegistro;
        private String mensaje;
        
        // Builder pattern
        public static UserResponseBuilder builder() {
            return new UserResponseBuilder();
        }
        
        public static class UserResponseBuilder {
            private UserResponse response = new UserResponse();
            
            public UserResponseBuilder id(Long id) {
                response.id = id;
                return this;
            }
            
            public UserResponseBuilder nombres(String nombres) {
                response.nombres = nombres;
                return this;
            }
            
            public UserResponseBuilder apellidos(String apellidos) {
                response.apellidos = apellidos;
                return this;
            }
            
            public UserResponseBuilder correoElectronico(String correoElectronico) {
                response.correoElectronico = correoElectronico;
                return this;
            }
            
            public UserResponseBuilder fechaRegistro(String fechaRegistro) {
                response.fechaRegistro = fechaRegistro;
                return this;
            }
            
            public UserResponseBuilder mensaje(String mensaje) {
                response.mensaje = mensaje;
                return this;
            }
            
            public UserResponse build() {
                return response;
            }
        }
        
        // Getters
        public Long getId() { return id; }
        public String getNombres() { return nombres; }
        public String getApellidos() { return apellidos; }
        public String getCorreoElectronico() { return correoElectronico; }
        public String getFechaRegistro() { return fechaRegistro; }
        public String getMensaje() { return mensaje; }
    }
    
    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        private String mensaje;
        private String codigo;
        
        // Builder pattern
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }
        
        public static class ErrorResponseBuilder {
            private ErrorResponse response = new ErrorResponse();
            
            public ErrorResponseBuilder mensaje(String mensaje) {
                response.mensaje = mensaje;
                return this;
            }
            
            public ErrorResponseBuilder codigo(String codigo) {
                response.codigo = codigo;
                return this;
            }
            
            public ErrorResponse build() {
                return response;
            }
        }
        
        // Getters
        public String getMensaje() { return mensaje; }
        public String getCodigo() { return codigo; }
    }
    
    /**
     * Response DTO for users list
     */
    public static class UsersListResponse {
        private List<UserResponse> usuarios;
        private int total;
        private String mensaje;
        
        // Builder pattern
        public static UsersListResponseBuilder builder() {
            return new UsersListResponseBuilder();
        }
        
        public static class UsersListResponseBuilder {
            private UsersListResponse response = new UsersListResponse();
            
            public UsersListResponseBuilder usuarios(List<UserResponse> usuarios) {
                response.usuarios = usuarios;
                return this;
            }
            
            public UsersListResponseBuilder total(int total) {
                response.total = total;
                return this;
            }
            
            public UsersListResponseBuilder mensaje(String mensaje) {
                response.mensaje = mensaje;
                return this;
            }
            
            public UsersListResponse build() {
                return response;
            }
        }
        
        // Getters
        public List<UserResponse> getUsuarios() { return usuarios; }
        public int getTotal() { return total; }
        public String getMensaje() { return mensaje; }
    }
}
