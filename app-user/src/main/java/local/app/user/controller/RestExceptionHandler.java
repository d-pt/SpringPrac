package local.app.user.controller;

import local.app.user.exception.AppNoRecordFound;
import local.app.user.exception.ErrorMesssage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String NO_RECORD_FOUND = "Application msg: Record does not exist";

    // API

    // 400

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
        final String bodyOfResponse = "This should be application specific";
        log.info("ConstraintViolationException:", ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
        final String bodyOfResponse = "This should be application specific";
        log.info("DataIntegrityViolationException:", ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String bodyOfResponse = "Application msg: Either Massage does not exist or not readable";
        // ex.getCause() instanceof JsonMappingException, JsonParseException // for additional information later on
        log.info("HttpMessageNotReadableException:", ex);
        ErrorMesssage errorMesssage = ErrorMesssage.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message("Invalid Input")
                .detail(bodyOfResponse)
                .build();
        return handleExceptionInternal(ex, errorMesssage, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     *
     * When bean validation fails.
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String bodyOfResponse = "Provided data is incorrect";
        log.info("MethodArgumentNotValidException:", ex);
        ErrorMesssage errorMesssage = ErrorMesssage.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message(NO_RECORD_FOUND)
                .detail(bodyOfResponse)
                .build();
        return handleExceptionInternal(ex, errorMesssage, headers, HttpStatus.BAD_REQUEST, request);
    }


    // 404

    @ExceptionHandler(value = { EntityNotFoundException.class, AppNoRecordFound.class })
    protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = NO_RECORD_FOUND;
        log.info("EntityNotFoundException:", ex);
        ErrorMesssage errorMesssage = ErrorMesssage.builder()
                .errorCode(404)
                .message(NO_RECORD_FOUND)
                .detail("Pass correct params")
                .build();
        return handleExceptionInternal(ex, errorMesssage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

  /*
    Below method can be used when this class does not extends   ResponseEntityExceptionHandler
    Else ith throws Caused by: java.lang.IllegalStateException: Ambiguous @ExceptionHandler method mapped for [class org.springframework.web.servlet.NoHandlerFoundException]
    //added spring.mvc.throw-exception-if-no-handler-found=true
    @ExceptionHandler(value = { NoHandlerFoundException.class })
    protected ResponseEntity<Object> handleGlobalException(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Application msg: Invalid URL ";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }*/


    // 409

    @ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
    protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // 412

    // 500

    @ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
    /*500*/public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        final String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
