package com.aiProject.exception;

public class OllamaInvokeException extends Exception {
    public OllamaInvokeException(String message) {
        super(message);
    }
    public OllamaInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
