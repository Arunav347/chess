package com.hardik.chess.service;

import com.hardik.chess.model.Board;
import com.hardik.chess.model.Cell;
import com.hardik.chess.model.Piece;
import com.hardik.chess.enumclass.PieceType;

public class PieceBishop extends Piece
{

    public PieceBishop(boolean white) 
    {
        super(white, PieceType.BISHOP);
    }

    //@Override
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

        int x = Math.abs(srcx-destx) ;
        int y = Math.abs(srcy-desty) ;

        if(x!=y) 
            return false ;
        
        int[] dx = {1,1,-1,-1} ;
        int[] dy = {1,-1,-1,1} ;

        int idx=-1 ;
        if(destx-srcx>0 && desty-srcy>0)
            idx=0 ;
        
        else if(destx-srcx>0 && desty-srcy<0)
            idx=1 ;
        
        else if(destx-srcx<0 && desty-srcy<0)
            idx=2 ;
        
        else if(destx-srcx<0 && desty-srcy>0)
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
