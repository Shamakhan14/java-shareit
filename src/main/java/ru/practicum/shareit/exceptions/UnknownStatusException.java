package ru.practicum.shareit.exceptions;

public class UnknownStatusException extends RuntimeException {

    public UnknownStatusException(String message) {
        super(message);
    }
}
