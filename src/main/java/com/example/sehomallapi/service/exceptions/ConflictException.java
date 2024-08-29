package com.example.sehomallapi.service.exceptions;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException{
    private final String detailMessage;
    private final String request;

    public ConflictException(String detailMessage, String request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
