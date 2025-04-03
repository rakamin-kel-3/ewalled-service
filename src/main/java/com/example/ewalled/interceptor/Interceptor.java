package com.example.ewalled.interceptor;

import com.example.ewalled.entity.HttpResponse;
import com.example.ewalled.exception.DataAlreadyExistException;
import com.example.ewalled.exception.DataNotFoundException;
import com.example.ewalled.exception.ForbiddenException;
import com.example.ewalled.exception.InsufficientBalanceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class Interceptor {
    @ExceptionHandler({
            RuntimeException.class,
    })
    public ResponseEntity<HttpResponse> handleException(RuntimeException ex){
        log.error("Handle Exception error : {} | {}", ex.getMessage(), ex.getStackTrace()[0]);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        HttpResponse.sendErrorResponse(ex.getMessage())
                );
    }

    @ExceptionHandler({
            DataAlreadyExistException.class
    })
    public ResponseEntity<HttpResponse> handleException(DataAlreadyExistException ex){
        log.error("Handle Exception error : {} | {}", ex.getMessage(), ex.getStackTrace()[0]);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        HttpResponse.sendErrorResponse(ex.getMessage())
                );
    }

    @ExceptionHandler({
            DataNotFoundException.class
    })
    public ResponseEntity<HttpResponse> handleException(DataNotFoundException ex){
        log.error("Handle Exception error : {} | {}", ex.getMessage(), ex.getStackTrace()[0]);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        HttpResponse.sendErrorResponse(ex.getMessage())
                );
    }

    @ExceptionHandler({
            ForbiddenException.class
    })
    public ResponseEntity<HttpResponse> handleException(ForbiddenException ex){
        log.error("Handle Exception error : {} | {}", ex.getMessage(), ex.getStackTrace()[0]);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        HttpResponse.sendErrorResponse(ex.getMessage())
                );
    }

    @ExceptionHandler({
            InsufficientBalanceException.class
    })
    public ResponseEntity<HttpResponse> handleException(InsufficientBalanceException ex){
        log.error("Handle Exception error : {} | {}", ex.getMessage(), ex.getStackTrace()[0]);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        HttpResponse.sendErrorResponse(ex.getMessage())
                );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<HttpResponse> handleException(MethodArgumentNotValidException ex){
        log.error("Handle Exception error : {} | {}", ex.getMessage(), ex.getStackTrace()[0]);
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        HttpResponse.sendErrorResponse(messages.toString())
                );
    }
}
