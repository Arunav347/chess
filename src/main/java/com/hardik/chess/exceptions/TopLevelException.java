package com.hardik.chess.exceptions;

import lombok.Data;

@Data
public class TopLevelException extends  Exception {
    String status;
    String message;
}
