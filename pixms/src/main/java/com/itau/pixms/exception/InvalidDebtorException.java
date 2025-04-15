package com.itau.pixms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class InvalidDebtorException extends PixMsException {

    private final String detail;

    public InvalidDebtorException(String detail) {
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        pd.setTitle("Invalid Debtor");
        pd.setDetail(detail);

        return pd;
    }
}
