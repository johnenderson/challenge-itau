package com.itau.pixms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public abstract class PixMsException extends RuntimeException {

    public PixMsException(Throwable cause) {
        super(cause);
    }

    public PixMsException(String message) {
        super(message);
    }

    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        pd.setTitle("Pix Microservice Internal Service Error");
        pd.setDetail("Contact Pix Microservice Support");

        return pd;
    }

}
