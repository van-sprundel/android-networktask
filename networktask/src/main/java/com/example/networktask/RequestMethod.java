package com.example.networktask;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String method;

    RequestMethod(String method) {
        this.method = method;
    }

    public String get() {
        return method;
    }
}