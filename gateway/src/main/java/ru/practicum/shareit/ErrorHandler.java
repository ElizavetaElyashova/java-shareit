package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.IllegalDateException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalDate(IllegalDateException e) {
        return new ErrorResponse(e.getMessage());
    }

}
