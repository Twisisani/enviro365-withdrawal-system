package com.enviro.assessment.junior.twisisanikhosa.exception;

/**
 * InvalidWithdrawalException - Thrown when a withdrawal request violates business rules
 * 
 * This custom exception is thrown when a withdrawal request fails one of the
 * system's business rule validations:
 * - Investor age is not greater than 65 for retirement products
 * - Requested amount exceeds current product balance
 * - Requested amount exceeds 90% of current balance
 * 
 * When caught by GlobalExceptionHandler, it results in an HTTP 400 response.
 */
public class InvalidWithdrawalException extends RuntimeException {
    
    /**
     * Constructor with error message
     * 
     * @param message Descriptive error message explaining the withdrawal rule violation
     */
    public InvalidWithdrawalException(String message) {
        super(message);
    }
}