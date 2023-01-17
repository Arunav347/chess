package com.hardik.chess.model;

import com.hardik.chess.enumclass.PieceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Piece {
    private Boolean isWhite  ;
    private Boolean isAlive ;
    private PieceType Name ;

    public Piece(Boolean white,PieceType name)
    {
        this.isWhite = white ;
        this.isAlive = true ;
        this.Name = name ;
    }

    public abstract boolean validMove(Board obj, Cell src, Cell dest) ;
}
