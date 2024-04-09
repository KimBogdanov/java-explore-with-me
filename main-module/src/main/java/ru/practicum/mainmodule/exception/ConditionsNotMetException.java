package ru.practicum.mainmodule.exception;

public class ConditionsNotMetException extends RuntimeException{
    public ConditionsNotMetException(String message) {
        super(message);
    }
}
