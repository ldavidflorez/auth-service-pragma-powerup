package co.com.powerup.pragma.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UseCasesConfigTest {

    @Test
    void testUseCasesConfigExists() {
        // Verify that the UseCasesConfig class can be instantiated
        UseCasesConfig config = new UseCasesConfig();
        assertNotNull(config, "UseCasesConfig should not be null");
    }
}