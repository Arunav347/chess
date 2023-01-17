package com.hardik.chess.service;

import com.hardik.chess.model.Board;
import com.hardik.chess.model.Cell;
import com.hardik.chess.model.Piece;
import com.hardik.chess.enumclass.PieceType;

public class PiecePawn extends Piece
{
    private boolean firstMove ;

    public PiecePawn(boolean white) 
    {
        super(white,  PieceType.PAWN);
    }
    public boolean getFirstMove()
    {
        return !(this.firstMove) ;
    }
    public void setFirstMove()
    {
        this.firstMove = true ;
    }

    public boolean validMove(Board boardobj, Cell src, Cell dest) 
    {
        if(src.getPieceObj()==null || src.getPosObj() == dest.getPosObj())
            return false ; 
            
        if(dest.getPieceObj()!=null && dest.getPieceObj().getIsWhite() == src.getPieceObj().getIsWhite())
            return false;

        int srcy = (int)(src.getPosObj().getCh()-'A') ;
        int desty = (int)(dest.getPosObj().getCh()-'A') ;
        int srcx = src.getPosObj().getI()-1 ;
        int destx = dest.getPosObj().getI()-1 ;

        int x = Math.abs(destx-srcx) ;
        int signx = destx-srcx ;
        int y = Math.abs(desty-srcy) ;

        if(x==0)
            return false ;

        int sign = (destx-srcx)/x ;

        if(src.getPieceObj().getIsWhite() == true && signx>0 || src.getPieceObj().getIsWhite() == false && signx<0 )
            return false ;

        //Diagonal capture
        if(x==1 && y==1 && dest.getPieceObj()!=null && dest.getPieceObj().getIsWhite()!=src.getPieceObj().getIsWhite())
            return true ;

        if(x>2 || y!=0 || (getFirstMove()==false && x==2))
            return false ;

        for(int i=1 ; i<=x && x<=2 ; i++)
        {
            if(boardobj.getCellInfo(srcx+(sign)*i, srcy).getPieceObj() != null )
                return false ;
        }

        setFirstMove() ;

        return true ;
    }
    
}
