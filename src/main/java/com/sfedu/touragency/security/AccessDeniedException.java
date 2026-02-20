package com.sfedu.touragency.security;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Access denied to the given resource");
    }
}
