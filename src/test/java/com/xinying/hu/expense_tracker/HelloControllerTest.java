package com.xinying.hu.expense_tracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloControllerTest {
    HelloController helloController;

    @BeforeEach
    void setUp() {
        this.helloController = new HelloController();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testIndex() {
        String expectedValue = "Greetings from Sprint Boot!";
        String actualValue = this.helloController.index();
        assertEquals(expectedValue, actualValue);
    }
}
