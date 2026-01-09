package com.lawbackend2.lawbackend2.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordUtilTest {

    @InjectMocks
    private PasswordUtil passwordUtil;

    private static final String RAW_PASSWORD = "TestPassword123";
    private static final String WRONG_PASSWORD = "WrongPassword456";

    private String encodedPassword;

    @BeforeEach
    void setUp() {
        encodedPassword = passwordUtil.encode(RAW_PASSWORD);
    }

    @Test
    void testEncodePassword() {
        assertNotNull(encodedPassword);
        assertNotEquals(RAW_PASSWORD, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$"));
    }

    @Test
    void testEncodeProducesDifferentHashes() {
        String encoded1 = passwordUtil.encode(RAW_PASSWORD);
        String encoded2 = passwordUtil.encode(RAW_PASSWORD);

        assertNotEquals(encoded1, encoded2);
    }

    @Test
    void testMatches_CorrectPassword() {
        boolean matches = passwordUtil.matches(RAW_PASSWORD, encodedPassword);

        assertTrue(matches);
    }

    @Test
    void testMatches_WrongPassword() {
        boolean matches = passwordUtil.matches(WRONG_PASSWORD, encodedPassword);

        assertFalse(matches);
    }

    @Test
    void testMatches_NullRawPassword() {
        boolean matches = passwordUtil.matches(null, encodedPassword);

        assertFalse(matches);
    }

    @Test
    void testMatches_NullEncodedPassword() {
        boolean matches = passwordUtil.matches(RAW_PASSWORD, null);

        assertFalse(matches);
    }

    @Test
    void testMatches_EmptyPassword() {
        boolean matches = passwordUtil.matches("", encodedPassword);

        assertFalse(matches);
    }

    @Test
    void testEncodeEmptyPassword() {
        String emptyEncoded = passwordUtil.encode("");

        assertNotNull(emptyEncoded);
        assertNotEquals("", emptyEncoded);
    }

    @Test
    void testEncodeNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordUtil.encode(null);
        });
    }

    @Test
    void testConsistentEncoding() {
        String encoded1 = passwordUtil.encode(RAW_PASSWORD);
        String encoded2 = passwordUtil.encode(RAW_PASSWORD);

        assertNotEquals(encoded1, encoded2);

        assertTrue(passwordUtil.matches(RAW_PASSWORD, encoded1));
        assertTrue(passwordUtil.matches(RAW_PASSWORD, encoded2));
    }
}
