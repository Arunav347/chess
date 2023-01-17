package com.hardik.chess.service;

import com.hardik.chess.model.Board;
import com.hardik.chess.model.Cell;
import com.hardik.chess.model.Piece;
import com.hardik.chess.enumclass.PieceType;

public class PieceRook extends Piece 
{
    public PieceRook(boolean white) 
    {
        super(white, PieceType.ROOK);
    }

    public boolean validMove(Board boardobj, Cell src, Cell dest) 
    {
        if(src.getPieceObj()==null || src.getPosObj() == dest.getPosObj())
            return false ; 

        if(dest.getPieceObj()!=null && dest.getPieceObj().getIsWhite() == src.getPieceObj().getIsWhite())
            return false;

        int srcy = (int)(src.getPosObj().getCh()-'A') ;
        int srcx = src.getPosObj().getI()-1 ;

        int desty = (int)(dest.getPosObj().getCh()-'A') ;
        int destx = dest.getPosObj().getI()-1 ;

        if(srcx!=destx && srcy!=desty)
            return false ;

        int[] dx = {1,0,-1,0} ;
        int[] dy = {0,-1,0,1} ;

        int idx=-1 ;
        if(destx-srcx>0 && desty==srcy)
            idx=0 ;
        
        else if(destx==srcx && desty-srcy<0)
            idx=1 ;
        
        else if(destx-srcx<0 && desty==srcy)
            idx=2 ;
        
        else if(destx==srcx && desty-srcy>0)
            idx=3 ;
            
        while(true)
        {
            int newx = srcx+dx[idx] ;
            int newy = srcy+dy[idx] ;

            if(newx==destx && newy==desty)
                break ;

            if(boardobj.getCellInfo(newx,newy).getPieceObj()!=null )
                return false ;
            
            srcx = newx ;
            srcy = newy ;           
        }

        return true ;
    }
    
}
