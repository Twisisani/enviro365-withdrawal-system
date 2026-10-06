package com.enviro.assessment.junior.twisisanikhosa.exception;

// Thrown when withdrawal request violates business rules
public class InvalidWithdrawalException extends RuntimeException {
    public InvalidWithdrawalException(String message) {
        super(message);
    }
}