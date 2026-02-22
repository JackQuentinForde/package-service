package com.example.packageservice.exception;

public class PackageNotFoundException extends RuntimeException {
    public PackageNotFoundException(String id) {
        super(String.format("A package with id %s does not exist", id));
    }
}