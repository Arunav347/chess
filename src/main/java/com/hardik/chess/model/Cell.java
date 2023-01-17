package com.hardik.chess.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Cell {
    private Piece pieceObj ;
    private Position posObj ;

    public Cell(Piece Pobj,Position posobj)
    {
        pieceObj = Pobj;
        posObj  = posobj;
    }
}
