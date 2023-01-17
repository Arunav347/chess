package com.hardik.chess.service;

import com.hardik.chess.model.Board;
import com.hardik.chess.model.Cell;
import com.hardik.chess.model.Piece;
import com.hardik.chess.enumclass.PieceType;

public class PieceQueen extends Piece 
{
    private static char blackSymbol = '♛' ;
    private static char whiteSymbol = '♕' ;

    public PieceQueen(boolean white) 
    {
        super(white, PieceType.QUEEN);
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
    
        int x = Math.abs(srcx-destx) ;
        int y = Math.abs(srcy-desty) ;

        if(srcx!=destx && srcy!=desty && x!=y) 
            return false ;

        int[] dx = {1,1,-1,-1,1,0,-1,0} ;
        int[] dy = {1,-1,-1,1,0,-1,0,1} ;

        int idx=-1 ;
        if(destx-srcx>0 && desty-srcy>0)
            idx=0 ;
        
        else if(destx-srcx>0 && desty-srcy<0)
            idx=1 ;
        
        else if(destx-srcx<0 && desty-srcy<0)
            idx=2 ;
        
        else if(destx-srcx<0 && desty-srcy>0)
            idx=3 ;
        
        else if(destx-srcx>0 && desty==srcy)
            idx=4 ;
        
        else if(destx==srcx && desty-srcy<0)
            idx=5 ;
        
        else if(destx-srcx<0 && desty==srcy)
            idx=6 ;
        
        else if(destx==srcx && desty-srcy>0)
            idx=7 ;
            
        while(true)
        {
            int newx = srcx+dx[idx] ;
            int newy = srcy+dy[idx] ;

            if(newx==destx && newy==desty)
                break ;

            if(boardobj.getCellInfo(newx,newy).getPieceObj()!=null)
                return false ;

            srcx = newx ;
            srcy = newy ;           
        }    

        return true ;
    }
    
}
