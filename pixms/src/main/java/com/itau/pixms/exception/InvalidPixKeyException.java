package com.itau.pixms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class InvalidPixKeyException extends PixMsException {

    private final String detail;

    public InvalidPixKeyException(String detail) {
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        pd.setTitle("Invalid Pix Key");
        pd.setDetail(detail);

        return pd;
    }
}
