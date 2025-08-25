package co.com.powerup.pragma.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Test
    void testUserBuilder() {
        // Arrange & Act
        User user = User.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .correoElectronico("juan.perez@test.com")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .salarioBase(5000000.0)
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .fechaRegistro(LocalDate.now())
                .build();

        // Assert
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Juan", user.getNombres());
        assertEquals("Pérez", user.getApellidos());
        assertEquals("juan.perez@test.com", user.getCorreoElectronico());
        assertEquals(LocalDate.of(1990, 5, 15), user.getFechaNacimiento());
        assertEquals(5000000.0, user.getSalarioBase());
        assertEquals("3001234567", user.getTelefono());
        assertEquals("Calle 123 #45-67", user.getDireccion());
        assertNotNull(user.getFechaRegistro());
    }

    @Test
    void testUserSettersAndGetters() {
        // Arrange
        User user = new User();

        // Act
        user.setId(2L);
        user.setNombres("María");
        user.setApellidos("García");
        user.setCorreoElectronico("maria.garcia@test.com");
        user.setFechaNacimiento(LocalDate.of(1985, 8, 20));
        user.setSalarioBase(6000000.0);
        user.setTelefono("3109876543");
        user.setDireccion("Avenida 456 #78-90");
        user.setFechaRegistro(LocalDate.now());

        // Assert
        assertEquals(2L, user.getId());
        assertEquals("María", user.getNombres());
        assertEquals("García", user.getApellidos());
        assertEquals("maria.garcia@test.com", user.getCorreoElectronico());
        assertEquals(LocalDate.of(1985, 8, 20), user.getFechaNacimiento());
        assertEquals(6000000.0, user.getSalarioBase());
        assertEquals("3109876543", user.getTelefono());
        assertEquals("Avenida 456 #78-90", user.getDireccion());
        assertNotNull(user.getFechaRegistro());
    }

    @Test
    void testSetFechaRegistro() {
        // Arrange
        User user = new User();
        LocalDate beforeCall = LocalDate.now();

        // Act
        user.setFechaRegistro();

        // Assert
        assertNotNull(user.getFechaRegistro());
        assertTrue(user.getFechaRegistro().isAfter(beforeCall.minusDays(1)) || 
                  user.getFechaRegistro().isEqual(beforeCall));
    }

    @Test
    void testUserWithNullValues() {
        // Arrange & Act
        User user = User.builder()
                .id(3L)
                .nombres("Carlos")
                .apellidos("López")
                .correoElectronico("carlos.lopez@test.com")
                .fechaNacimiento(LocalDate.of(1992, 3, 10))
                .salarioBase(4500000.0)
                .build();

        // Assert
        assertNotNull(user);
        assertEquals(3L, user.getId());
        assertEquals("Carlos", user.getNombres());
        assertEquals("López", user.getApellidos());
        assertEquals("carlos.lopez@test.com", user.getCorreoElectronico());
        assertEquals(LocalDate.of(1992, 3, 10), user.getFechaNacimiento());
        assertEquals(4500000.0, user.getSalarioBase());
        assertNull(user.getTelefono());
        assertNull(user.getDireccion());
        assertNull(user.getFechaRegistro());
    }

    @Test
    void testUserToBuilder() {
        // Arrange
        User originalUser = User.builder()
                .id(1L)
                .nombres("Original")
                .apellidos("User")
                .correoElectronico("original@test.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .salarioBase(1000000.0)
                .build();

        // Act
        User modifiedUser = originalUser.toBuilder()
                .id(2L)
                .nombres("Modified")
                .correoElectronico("modified@test.com")
                .build();

        // Assert
        assertNotEquals(originalUser.getId(), modifiedUser.getId());
        assertNotEquals(originalUser.getNombres(), modifiedUser.getNombres());
        assertNotEquals(originalUser.getCorreoElectronico(), modifiedUser.getCorreoElectronico());
        assertEquals(originalUser.getApellidos(), modifiedUser.getApellidos());
        assertEquals(originalUser.getFechaNacimiento(), modifiedUser.getFechaNacimiento());
        assertEquals(originalUser.getSalarioBase(), modifiedUser.getSalarioBase());
    }

    @Test
    void testUserEquality() {
        // Arrange
        User user1 = User.builder()
                .id(1L)
                .nombres("Test")
                .apellidos("User")
                .correoElectronico("test@test.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .salarioBase(1000000.0)
                .build();

        User user2 = User.builder()
                .id(1L)
                .nombres("Test")
                .apellidos("User")
                .correoElectronico("test@test.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .salarioBase(1000000.0)
                .build();

        // Act & Assert
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getNombres(), user2.getNombres());
        assertEquals(user1.getApellidos(), user2.getApellidos());
        assertEquals(user1.getCorreoElectronico(), user2.getCorreoElectronico());
        assertEquals(user1.getFechaNacimiento(), user2.getFechaNacimiento());
        assertEquals(user1.getSalarioBase(), user2.getSalarioBase());
    }

    @Test
    void testUserWithSpecialCharacters() {
        // Arrange & Act
        User user = User.builder()
                .id(4L)
                .nombres("José María")
                .apellidos("García-López")
                .correoElectronico("jose.maria+tag@domain.co.uk")
                .fechaNacimiento(LocalDate.of(1988, 12, 25))
                .salarioBase(7500000.0)
                .telefono("+573001234567")
                .direccion("Calle 123 #45-67, Apto 101")
                .build();

        // Assert
        assertNotNull(user);
        assertEquals("José María", user.getNombres());
        assertEquals("García-López", user.getApellidos());
        assertEquals("jose.maria+tag@domain.co.uk", user.getCorreoElectronico());
        assertEquals("+573001234567", user.getTelefono());
        assertEquals("Calle 123 #45-67, Apto 101", user.getDireccion());
    }
}
