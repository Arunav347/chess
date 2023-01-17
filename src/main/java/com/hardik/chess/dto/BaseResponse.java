package com.hardik.chess.dto;

import lombok.Data;

@Data
public class BaseResponse {

    String status;
    String message;

    public BaseResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
