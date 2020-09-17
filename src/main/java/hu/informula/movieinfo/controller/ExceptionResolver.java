package hu.informula.movieinfo.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import java.util.HashMap;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class ExceptionResolver {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HashMap<String, String> handleNoHandlerFound(NoHandlerFoundException e, WebRequest request) {
        HashMap<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        response.put("message", e.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    HashMap<String, String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request) {
        HashMap<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        response.put("message", e.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    HashMap<String, String> handleHttpClientException(HttpClientErrorException e, WebRequest request) {
        HashMap<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        response.put("message", e.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    HashMap<String, String> handleHttpServerException(HttpServerErrorException e, WebRequest request) {
        HashMap<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        response.put("message", e.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(NotImplementedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    HashMap<String, String> handleRuntimeException(NotImplementedException e, WebRequest request) {
        HashMap<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        response.put("message", e.getLocalizedMessage());
        return response;
    }
}