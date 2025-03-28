package com.example.ewalled.interceptor;

import com.example.ewalled.entity.HttpResponse;
import com.example.ewalled.exception.DataAlreadyExistException;
import com.example.ewalled.exception.DataNotFoundException;
import com.example.ewalled.exception.ForbiddenException;
import com.example.ewalled.exception.InsufficientBalanceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Interceptor {
    @ExceptionHandler({
            RuntimeException.class,
    })
    public ResponseEntity<HttpResponse> handleException(RuntimeException ex){
        ex.printStackTrace();
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
        ex.printStackTrace();
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
        ex.printStackTrace();
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
        ex.printStackTrace();
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
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        HttpResponse.sendErrorResponse(ex.getMessage())
                );
    }
}
