package com.fiso.nleya.marker.shared.exceptions;

public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6253723875428L;

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String item, long id) {
        super(item+" not found , with id "+id);
    }
}
