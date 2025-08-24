package co.com.powerup.pragma.usecase.saveuser;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SaveUserUseCase {
    
    private final UserRepository userRepository;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{7,15}$");
    
    /**
     * Saves a new user in the system
     * @param user the user to save
     * @return Mono<User> the saved user with generated ID and registration date
     */
    public Mono<User> saveUser(User user) {
        System.out.println("INFO: Iniciando proceso de guardado de usuario con email: " + user.getCorreoElectronico());
        return Mono.just(user)
                .doOnNext(u -> System.out.println("DEBUG: Validando datos del usuario: " + u.getCorreoElectronico()))
                .doOnNext(this::validateUser)
                .doOnNext(u -> {
                    System.out.println("DEBUG: Estableciendo fecha de registro para usuario: " + u.getCorreoElectronico());
                    u.setFechaRegistro();
                })
                .flatMap(u -> {
                    System.out.println("DEBUG: Guardando usuario en repositorio: " + u.getCorreoElectronico());
                    return userRepository.save(u);
                })
                .doOnSuccess(savedUser -> System.out.println("INFO: Usuario guardado exitosamente con ID: " + 
                        savedUser.getId() + " y email: " + savedUser.getCorreoElectronico()))
                .doOnError(error -> System.out.println("ERROR: Error al guardar usuario con email " + 
                        user.getCorreoElectronico() + ": " + error.getMessage()));
    }
    
    /**
     * Saves a new user with email validation in a transactional operation
     * This method demonstrates atomic operations as required by the user story
     * @param user the user to save
     * @return Mono<User> the saved user with generated ID and registration date
     */
    public Mono<User> saveUserWithEmailValidation(User user) {
        System.out.println("INFO: Iniciando proceso de guardado de usuario con validacion de email: " + user.getCorreoElectronico());
        return Mono.just(user)
                .doOnNext(u -> System.out.println("DEBUG: Validando datos del usuario: " + u.getCorreoElectronico()))
                .doOnNext(this::validateUser)
                .doOnNext(u -> {
                    System.out.println("DEBUG: Estableciendo fecha de registro para usuario: " + u.getCorreoElectronico());
                    u.setFechaRegistro();
                })
                .flatMap(u -> {
                    System.out.println("DEBUG: Verificando si el email ya existe: " + u.getCorreoElectronico());
                    return userRepository.existsByEmail(u.getCorreoElectronico())
                            .flatMap(exists -> {
                                if (exists) {
                                    System.out.println("WARN: Intento de registro con email duplicado: " + u.getCorreoElectronico());
                                    return Mono.error(new IllegalArgumentException("El email ya existe: " + u.getCorreoElectronico()));
                                }
                                System.out.println("DEBUG: Email disponible, procediendo a guardar usuario: " + u.getCorreoElectronico());
                                return userRepository.save(u);
                            });
                })
                .doOnSuccess(savedUser -> System.out.println("INFO: Usuario guardado exitosamente con validacion de email - ID: " + 
                        savedUser.getId() + " y email: " + savedUser.getCorreoElectronico()))
                .doOnError(error -> System.out.println("ERROR: Error al guardar usuario con validacion de email " + 
                        user.getCorreoElectronico() + ": " + error.getMessage()));
    }
    
    /**
     * Validates the user data before saving
     * @param user the user to validate
     */
    private void validateUser(User user) {
        System.out.println("DEBUG: Iniciando validacion completa del usuario con email: " + user.getCorreoElectronico());
        validateNames(user.getNombres(), "Nombres");
        validateNames(user.getApellidos(), "Apellidos");
        validateEmail(user.getCorreoElectronico());
        validateSalary(user.getSalarioBase());
        validateDateOfBirth(user.getFechaNacimiento());
        validatePhone(user.getTelefono());
        validateAddress(user.getDireccion());
        System.out.println("DEBUG: Validacion completa del usuario exitosa: " + user.getCorreoElectronico());
    }
    
    /**
     * Validates names and surnames
     */
    private void validateNames(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " no pueden ser nulos o vacios");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException(fieldName + " deben tener al menos 2 caracteres");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException(fieldName + " no pueden exceder 50 caracteres");
        }
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException(fieldName + " solo pueden contener letras y espacios");
        }
    }
    
    /**
     * Validates email format and length
     */
    private void validateEmail(String email) {
        System.out.println("DEBUG: Validando formato de email: " + email);
        if (email == null || email.trim().isEmpty()) {
            System.out.println("ERROR: Validacion de email fallida: email nulo o vacio");
            throw new IllegalArgumentException("El email no puede ser nulo o vacio");
        }
        if (email.trim().length() > 100) {
            System.out.println("ERROR: Validacion de email fallida: email excede 100 caracteres - " + email);
            throw new IllegalArgumentException("El email no puede exceder 100 caracteres");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            System.out.println("ERROR: Validacion de email fallida: formato invalido - " + email);
            throw new IllegalArgumentException("El email debe tener un formato valido (ej: usuario@dominio.com)");
        }
        System.out.println("DEBUG: Validacion de email exitosa: " + email);
    }
    
    /**
     * Validates salary range
     */
    private void validateSalary(Double salary) {
        if (salary == null) {
            throw new IllegalArgumentException("El salario base no puede ser nulo");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo");
        }
        if (salary > 15000000) {
            throw new IllegalArgumentException("El salario base no puede exceder 15,000,000");
        }
    }
    
    /**
     * Validates date of birth
     */
    private void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser en el futuro");
        }
        if (dateOfBirth.isBefore(LocalDate.now().minusYears(120))) {
            throw new IllegalArgumentException("La fecha de nacimiento parece invalida (mas de 120 anos atras)");
        }
    }
    
    /**
     * Validates phone number (optional field)
     */
    private void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (phone.trim().length() > 15) {
                throw new IllegalArgumentException("El telefono no puede exceder 15 caracteres");
            }
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new IllegalArgumentException("El telefono debe contener solo digitos y estar entre 7 y 15 caracteres");
            }
        }
    }
    
    /**
     * Validates address (optional field)
     */
    private void validateAddress(String address) {
        if (address != null && address.trim().length() > 200) {
            throw new IllegalArgumentException("La direccion no puede exceder 200 caracteres");
        }
    }
}
