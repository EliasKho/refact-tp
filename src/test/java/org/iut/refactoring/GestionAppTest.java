package org.iut.refactoring;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class GestionAppTest {

    @Test
    void testMainEstExecutable() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        GestionApp.main(new String[]{});

        System.setOut(original);

        String console = out.toString();
        assertTrue(console.contains("Salaire de Alice"));
        assertTrue(console.contains("=== LOGS ==="));
    }
}
