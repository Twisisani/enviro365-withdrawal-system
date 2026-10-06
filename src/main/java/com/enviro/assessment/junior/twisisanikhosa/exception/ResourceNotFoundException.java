package com.enviro.assessment.junior.twisisanikhosa.exception;

// Thrown when requested resource (investor/product) not found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}