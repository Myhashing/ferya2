package com.mlmfreya.ferya2.dto;

import lombok.Data;

@Data
public class TronAPIResponse<T> {
    private boolean success;
    private T result;
    private String error;

    // getters and setters
}
