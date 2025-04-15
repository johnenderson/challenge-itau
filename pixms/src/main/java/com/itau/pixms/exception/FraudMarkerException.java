package com.itau.pixms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.springframework.http.ProblemDetail.forStatus;

public class FraudMarkerException extends PixMsException {

    private final String detail;

    public FraudMarkerException(String detail) {
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail(){
        var pd = forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        pd.setTitle("Key Fraud Marker error");
        pd.setDetail(detail);

        return pd;
    }
}
