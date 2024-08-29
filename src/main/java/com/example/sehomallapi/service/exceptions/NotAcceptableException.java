package com.example.sehomallapi.service.exceptions;

import lombok.Getter;

@Getter
public class NotAcceptableException extends RuntimeException{
    private final String detailMessage;
    private final String request;

    public NotAcceptableException(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
