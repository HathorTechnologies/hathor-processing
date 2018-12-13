package com.hathor.docs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class APIError {

    private String message;

    public APIError(String message) {
        this.message = message;
    }

}
