package com.hardik.chess.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Position {
    private char ch ;
    private int i ;

    public Position(char ch,int i)
    {
        this.setCh(ch);
        this.setI(i);
    }
}
