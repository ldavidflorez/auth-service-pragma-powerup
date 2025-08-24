package co.com.powerup.pragma.api;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.usecase.saveuser.SaveUserUseCase;
import co.com.powerup.pragma.usecase.findbyemail.FindByEmailUseCase;
import co.com.powerup.pragma.usecase.listallusers.ListAllUsersUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs para gestion de usuarios")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final SaveUserUseCase saveUserUseCase;
    private final FindByEmailUseCase findByEmailUseCase;
    private final ListAllUsersUseCase listAllUsersUseCase;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema con validaciones de email unico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validacion",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public Mono<UserResponse> registerUser(@RequestBody User user) {
        logger.info("Recibida solicitud de registro de usuario");
        return saveUserUseCase.saveUserWithEmailValidation(user)
                .map(savedUser -> {
                    logger.info("Usuario registrado exitosamente - ID: {} y email: {}", savedUser.getId(), savedUser.getCorreoElectronico());
                    return UserResponse.builder()
                            .id(savedUser.getId())
                            .nombres(savedUser.getNombres())
                            .apellidos(savedUser.getApellidos())
                            .correoElectronico(savedUser.getCorreoElectronico())
                            .fechaRegistro(savedUser.getFechaRegistro() != null ? savedUser.getFechaRegistro().toString() : null)
                            .mensaje("Usuario registrado exitosamente")
                            .build();
                });
    }
    
    @GetMapping("/email")
    @Operation(
        summary = "Buscar usuario por email",
        description = "Busca un usuario especifico por su direccion de email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validacion - email requerido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public Mono<UserResponse> findUserByEmail(
            @Parameter(description = "Email del usuario a buscar", required = true, example = "juan.garcia@email.com")
            @RequestParam("email") String email) {
        logger.info("Recibida solicitud de busqueda de usuario por email: {}", email);
        return findByEmailUseCase.findByEmail(email)
                .map(user -> {
                    logger.info("Usuario encontrado exitosamente - ID: {} y email: {}", user.getId(), user.getCorreoElectronico());
                    return UserResponse.builder()
                            .id(user.getId())
                            .nombres(user.getNombres())
                            .apellidos(user.getApellidos())
                            .correoElectronico(user.getCorreoElectronico())
                            .fechaRegistro(user.getFechaRegistro() != null ? user.getFechaRegistro().toString() : null)
                            .mensaje("Usuario encontrado exitosamente")
                            .build();
                });
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todos los usuarios",
        description = "Obtiene una lista de todos los usuarios registrados en el sistema (stream reactivo)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                // mediaType = "text/event-stream",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public Flux<UserResponse> listAllUsers() {
        logger.info("Recibida solicitud de listado de todos los usuarios");
        return listAllUsersUseCase.listAllUsers()
                // .delayElements(Duration.ofMillis(1000)) // Delay para visualizar el flujo
                .doOnNext(user -> logger.info("Procesando usuario - ID: {} y email: {}", user.getId(), user.getCorreoElectronico()))
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .nombres(user.getNombres())
                        .apellidos(user.getApellidos())
                        .correoElectronico(user.getCorreoElectronico())
                        .fechaRegistro(user.getFechaRegistro() != null ? user.getFechaRegistro().toString() : null)
                        .build())
                .doOnNext(userResponse -> logger.info("Usuario mapeado - ID: {} y email: {}", userResponse.getId(), userResponse.getCorreoElectronico()));
    }
    
    /**
     * Response DTO for user operations
     */
    @Schema(description = "Respuesta de usuario")
    public static class UserResponse {
        @Schema(description = "ID único del usuario", example = "1")
        private Long id;
        
        @Schema(description = "Nombres del usuario", example = "Juan Carlos")
        private String nombres;
        
        @Schema(description = "Apellidos del usuario", example = "Garcia Lopez")
        private String apellidos;
        
        @Schema(description = "Correo electrónico del usuario", example = "juan.garcia@email.com")
        private String correoElectronico;
        
        @Schema(description = "Fecha de registro del usuario", example = "2025-08-24")
        private String fechaRegistro;
        
        @Schema(description = "Mensaje de respuesta", example = "Usuario registrado exitosamente")
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
    @Schema(description = "Respuesta de error")
    public static class ErrorResponse {
        @Schema(description = "Mensaje de error", example = "Error de validacion: El email ya existe")
        private String mensaje;
        
        @Schema(description = "Codigo de error", example = "VALIDATION_ERROR")
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
    @Schema(description = "Respuesta de lista de usuarios")
    public static class UsersListResponse {
        @Schema(description = "Lista de usuarios")
        private List<UserResponse> usuarios;
        
        @Schema(description = "Total de usuarios", example = "5")
        private int total;
        
        @Schema(description = "Mensaje de respuesta", example = "Listado de usuarios obtenido exitosamente")
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
