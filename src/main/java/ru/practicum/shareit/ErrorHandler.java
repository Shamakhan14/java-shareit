package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import javax.validation.ValidationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final ValidationException exception) {
        log.info(exception.getClass().toString() + ": " + exception.getMessage());
        return Map.of("error", "Ошибка валидации.",
                "error message", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherExceptions(final Throwable exception) {
        log.error(exception.getClass().toString() + ": " + exception.getMessage());
        return Map.of("error", exception.getClass().toString(),
                "error message", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUniqueException(final SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getClass().toString() + ": " + exception.getMessage());
        return Map.of("error", exception.getClass().toString(),
                "error message", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNullPointExceptions(final NullPointerException exception) {
        log.error(exception.getClass().toString() + ": " + exception.getMessage());
        return Map.of("error", exception.getClass().toString(),
                "error message", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle404(final UserNotFoundException exception) {
        log.info(exception.getClass().toString() + ": " + exception.getMessage());
        return Map.of("error", "Искомый объект не найден.",
                "error message", exception.getMessage());
    }
}
