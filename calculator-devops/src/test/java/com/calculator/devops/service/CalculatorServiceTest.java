package com.calculator.devops.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorServiceTest {

    @Test
    void shouldAddTwoNumbers() {
        CalculatorService calculatorService = new CalculatorService();

        double result = calculatorService.add(10, 20);

        assertEquals(30.0, result);
    }

    @Test
    void shouldSubtractTwoNumbers() {
        CalculatorService calculatorService = new CalculatorService();

        double result = calculatorService.subtract(20, 8);

        assertEquals(12.0, result);
    }

    @Test
    void shouldMultiplyTwoNumbers() {
        CalculatorService calculatorService = new CalculatorService();

        double result = calculatorService.multiply(6, 7);

        assertEquals(42.0, result);
    }

    @Test
    void shouldDivideTwoNumbers() {
        CalculatorService calculatorService = new CalculatorService();

        double result = calculatorService.divide(20, 4);

        assertEquals(5.0, result);
    }


}
