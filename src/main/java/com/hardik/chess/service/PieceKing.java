package com.hardik.chess.service;

import com.hardik.chess.model.Board;
import com.hardik.chess.model.Cell;
import com.hardik.chess.model.Piece;
import com.hardik.chess.enumclass.PieceType;

public class PieceKing extends Piece 
{
    private boolean isCheck ;
    
    public PieceKing(boolean white) 
    {
        super(white,PieceType.KING);
    }

    public boolean getKingCheck()
    {
        return this.isCheck ;
    }

    public void setKingCheck(boolean check)
    {
        this.isCheck = check ;
    }
    public boolean validMove(Board boardobj, Cell src, Cell dest) 
    {
        if(src.getPieceObj()==null || src.getPosObj() == dest.getPosObj())
            return false ;

        if(dest.getPieceObj()!=null && dest.getPieceObj().getIsWhite() == src.getPieceObj().getIsWhite())
            return false;

        int srcy = (int)(src.getPosObj().getCh()-'A') ;
        int srcx = src.getPosObj().getI() ;

        int desty = (int)(dest.getPosObj().getCh()-'A') ;
        int destx = dest.getPosObj().getI() ;

        int x = Math.abs(srcx-destx) ;
        int y = Math.abs(srcy-desty) ;

        return x+y<=2;
    }
    
}
