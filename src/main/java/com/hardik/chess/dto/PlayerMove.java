package com.hardik.chess.dto;

import lombok.Data;

@Data
public class PlayerMove {

    String source;

    String destination;

    String name;

    Boolean PlayerColor;
}
