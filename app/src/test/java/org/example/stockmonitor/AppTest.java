package org.example.stockmonitor; // Keep the package name as it is

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test
    void appRunsWithoutErrors() {
        assertDoesNotThrow(() -> org.example.stockmonitor.App.main(new String[]{}), "App should run without exceptions.");
    }
}
