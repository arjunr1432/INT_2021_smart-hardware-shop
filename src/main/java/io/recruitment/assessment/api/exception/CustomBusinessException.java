package io.recruitment.assessment.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomBusinessException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public CustomBusinessException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
        this.code = code;
    }
}
