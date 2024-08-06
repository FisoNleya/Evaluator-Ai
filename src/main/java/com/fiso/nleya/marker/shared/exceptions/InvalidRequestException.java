package com.fiso.nleya.marker.shared.exceptions;


public class InvalidRequestException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public InvalidRequestException(String message) {
        super(message);
    }
}
