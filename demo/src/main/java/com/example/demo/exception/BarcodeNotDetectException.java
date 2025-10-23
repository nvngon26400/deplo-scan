package com.example.demo.exception;

public class BarcodeNotDetectException extends RuntimeException {
    public BarcodeNotDetectException(String message) {
        super(message);
    }
}