package com.hardik.chess.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardik.chess.Dao.ChessGameDao;
import com.hardik.chess.dto.PlayerMove;
import com.hardik.chess.enumclass.GameStatus;
import com.hardik.chess.enumclass.PieceType;
import com.hardik.chess.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
;


import java.util.ArrayList;

@Component
public class ValidMoveService {

    @Autowired
    private ChessGameDao chessGameDaoImpl;

    @Autowired
    private ObjectMapper objectMapper;

    public String movePlayer(String gameId, PlayerMove playerMove) {
        String boardString;
        GameInformation gameInformation = chessGameDaoImpl.findGameId(gameId);
        Board board = null;
        try{
            board =  objectMapper.readValue(gameInformation.getBoard(), Board.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        char src_ch =playerMove.getSource().charAt(0) ;
        int src_i = (playerMove.getSource().charAt(1)-'0') ;
        char dest_ch = playerMove.getDestination().charAt(0) ;
        int dest_i = (playerMove.getDestination().charAt(1)-'0');
        Cell src_cell = board.getCellInfo(src_ch, src_i) ;
        Cell dest_cell = board.getCellInfo(dest_ch, dest_i) ;
        
        boolean isValid = this.CheckValid(board, src_cell,dest_cell,playerMove.getPlayerColor()) ;
        if(isValid == false)
        {
            return GameStatus.INVALID_MOVE.getValue();
        }

        boolean isKingOnCheck = this.makeMove(board, src_cell,dest_cell,playerMove) ;

        if(isKingOnCheck == true)
        {
            /*System.out.println("Invalid Move ---Check !!") ;
            PlayerList.addFirst(pobj);
            continue ;*/
            return GameStatus.KING_ON_CHECK.getValue();
        }
        boolean winner = false ;

        winner = this.CheckWinner(board, playerMove.getPlayerColor()) ;
        try {
            boardString = objectMapper.writeValueAsString(board);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        gameInformation.setBoard(boardString);
        chessGameDaoImpl.insertGame(gameInformation);

        if(winner)
        {

            return GameStatus.WINNER.getValue();
        }

        return GameStatus.CONTINUE.getValue();


    }

    public boolean CheckValid(Board board, Cell src_cell, Cell dest_cell,boolean PlayerColor)
    {
        return src_cell.getPieceObj()!=null &&
                src_cell.getPieceObj().validMove(board,src_cell, dest_cell) &&
                PlayerColor==src_cell.getPieceObj().getIsWhite() &&
                (dest_cell.getPieceObj()==null ||
                        dest_cell.getPieceObj().getIsWhite()!=src_cell.getPieceObj().getIsWhite() ) ;
    }

    public boolean makeMove(Board board, Cell src_cell,Cell dest_cell, PlayerMove pobj)
    {
        Cell src_curr_cell = src_cell ;
        Cell dest_curr_cell = dest_cell ;

        Piece src_piece = src_cell.getPieceObj() ;
        Piece dest_piece = dest_cell.getPieceObj() ;

        boolean capture = isCapture(dest_cell, pobj) ;

        if(capture==true)
        {
            Piece killPiece = dest_cell.getPieceObj() ;

            if(pobj.getPlayerColor()==true)
                System.out.println("Black "+killPiece.getName()+" Killed");
            else
                System.out.println("White "+killPiece.getName()+" Killed");

            if(dest_piece.getName()== PieceType.KING)
            {
                if(dest_piece.getIsWhite()==true)
                    board.getKingInfo()[1].setPieceObj(null);
                else
                    board.getKingInfo()[0].setPieceObj(null);
            }
            killPiece.setIsAlive(false);
            killPiece = null ;
        }

        src_cell.setPieceObj(null);
        dest_cell.setPieceObj(src_piece);

        if(src_piece.getName() == PieceType.KING)
        {
            if(pobj.getPlayerColor() == true)
                board.getKingInfo()[1] = dest_cell ;
            else
                board.getKingInfo()[0] = dest_cell ;
        }

        Cell KingCell ;
        // if(pobj.getPlayerColor()==true)
        //     KingCell = board.getKingInfo()[1] ;
        // else
        //     KingCell = board.getKingInfo()[0] ;

        boolean isMoveReal = true ;

        for(int i=0 ; i<board.getKingInfo().length ; i++)
        {
            KingCell = board.getKingInfo()[i] ;
            if(FindKingCheck(board, KingCell,isMoveReal) == true )
            {
                ((PieceKing)KingCell.getPieceObj()).setKingCheck(true);

                if(KingCell.getPieceObj().getIsWhite()==true)
                    System.out.println("White King is on CHECK !!") ;
                else
                    System.out.println("Black King is on CHECK !!") ;

                if((pobj.getPlayerColor() == true &&
                        ((PieceKing)board.getKingInfo()[1].getPieceObj()).getKingCheck() == true) ||
                        (pobj.getPlayerColor() == false &&
                                ((PieceKing)board.getKingInfo()[0].getPieceObj()).getKingCheck() == true))
                    ResetVirtualMove(board, src_curr_cell,dest_curr_cell,dest_piece) ;

            }
            else
            {
                if(KingCell.getPieceObj().getIsWhite()==true)
                    board.getKingCheckList()[1].clear() ;
                else
                    board.getKingCheckList()[0].clear() ;

                ((PieceKing)KingCell.getPieceObj()).setKingCheck(false);
            }
        }

        boolean col = pobj.getPlayerColor() ;

        if(col == true && ((PieceKing)board.getKingInfo()[1].getPieceObj()).getKingCheck() == true)
            return true ;

        if(col == false && ((PieceKing)board.getKingInfo()[0].getPieceObj()).getKingCheck() == true)
            return true ;

        return false ;
    }

    private void ResetVirtualMove(Board board, Cell src_curr_cell, Cell dest_curr_cell,Piece dest_Piece)
    {
        src_curr_cell.setPieceObj(dest_curr_cell.getPieceObj());
        dest_curr_cell.setPieceObj(dest_Piece);

        if(src_curr_cell.getPieceObj().getName() == PieceType.KING)
        {
            if(src_curr_cell.getPieceObj().getIsWhite() == true)
                board.getKingInfo()[1] = src_curr_cell ;
            else
                board.getKingInfo()[0] = src_curr_cell ;
        }
    }

    private boolean FindKingCheck(Board board, Cell kingCell,Boolean isMoveReal)
    {
        boolean isKnight = KnightCheck(board, kingCell,isMoveReal) ;
        boolean isRow = RowVerCheck(board, kingCell,isMoveReal) ;
        boolean isDiagonal = DiagonalCheck(board, kingCell,isMoveReal) ;

        if(isKnight || isRow || isDiagonal)
            return true ;

        return false ;
    }

    private boolean DiagonalCheck(Board board, Cell kingCell,boolean isMoveReal)
    {
        boolean isFromPawn = PawnCheck(board, kingCell,isMoveReal) ;

        if(isFromPawn)
            return true ;

        boolean kingColor = kingCell.getPieceObj().getIsWhite() ;

        int idx=0;
        if(kingColor == true)
            idx=1 ;

        int x = kingCell.getPosObj().getI()-1 ;
        int y = (int)(kingCell.getPosObj().getCh()-'A') ;

        int[] dx = {1,1,-1,-1} ;
        int[] dy = {1,-1,-1,1} ;

        for(int i=0 ; i<4 ; i++)
        {
            int newx = x+dx[i] ;
            int newy = y+dy[i] ;

            while(newx<8 && newx>=0 && newy<8 && newy>=0)
            {
                if(board.getCellobj()[newx][newy].getPieceObj()!=null &&
                        board.getCellobj()[newx][newy].getPieceObj().getIsWhite()!=kingColor &&
                        (board.getCellobj()[newx][newy].getPieceObj().getName() == PieceType.BISHOP ||
                                board.getCellobj()[newx][newy].getPieceObj().getName() == PieceType.QUEEN ))
                {
                    if(isMoveReal == true)
                        board.getKingCheckList()[idx].add(board.getCellobj()[newx][newy]) ;

                    return true ;
                }

                if(board.getCellobj()[newx][newy].getPieceObj()!=null && board.getCellobj()[newx][newy].getPieceObj().getIsWhite()==kingColor)
                    break ;

                newx += dx[i] ;
                newy += dy[i] ;
            }
        }

        return false;
    }

    private boolean PawnCheck(Board board, Cell kingCell,boolean isMoveReal)
    {
        boolean kingColor = kingCell.getPieceObj().getIsWhite() ;

        int idx=0 ;
        if(kingColor == true)
            idx=1 ;

        int x = kingCell.getPosObj().getI()-1 ;
        int y = (int)(kingCell.getPosObj().getCh()-'A') ;

        if(kingColor == false)
        {
            if(x+1<8 && y+1<8 &&
                    board.getCellobj()[x+1][y+1].getPieceObj()!=null &&
                    board.getCellobj()[x+1][y+1].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[x+1][y+1].getPieceObj().getName() == PieceType.PAWN)
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[x+1][y+1]) ;

                return true ;
            }

            if(x+1<8 && y-1>=0 &&
                    board.getCellobj()[x+1][y-1].getPieceObj()!=null &&
                    board.getCellobj()[x+1][y-1].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[x+1][y-1].getPieceObj().getName() == PieceType.PAWN)
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[x+1][y-1]) ;

                return true ;
            }
        }
        else
        {
            if(x-1>=0 && y-1>=0 &&
                    board.getCellobj()[x-1][y-1].getPieceObj()!=null &&
                    board.getCellobj()[x-1][y-1].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[x-1][y-1].getPieceObj().getName() == PieceType.PAWN)
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[x-1][y-1]) ;

                return true ;
            }

            if(x-1>=0 && y+1<8 &&
                    board.getCellobj()[x-1][y+1].getPieceObj()!=null &&
                    board.getCellobj()[x-1][y+1].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[x-1][y+1].getPieceObj().getName() == PieceType.PAWN)
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[x-1][y+1]) ;

                return true ;
            }
        }

        return false ;

    }

    private boolean RowVerCheck(Board board, Cell kingCell,boolean isMoveReal)
    {
        boolean kingColor = kingCell.getPieceObj().getIsWhite() ;

        int idx=0 ;
        if(kingColor == true)
            idx=1 ;

        int x = kingCell.getPosObj().getI()-1 ;
        int y = (int)(kingCell.getPosObj().getCh()-'A') ;

        //down
        for(int i=x+1 ; i<8 ; i++)
        {
            if(board.getCellobj()[i][y].getPieceObj()==null)
                continue;

            if(board.getCellobj()[i][y].getPieceObj().getIsWhite()!=kingColor &&
                    (board.getCellobj()[i][y].getPieceObj().getName() == PieceType.ROOK ||
                            board.getCellobj()[i][y].getPieceObj().getName() == PieceType.QUEEN ))
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[i][y]) ;

                return true ;
            }

            if(board.getCellobj()[i][y].getPieceObj().getIsWhite()==kingColor)
                break ;

            if((board.getCellobj()[i][y].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[i][y].getPieceObj().getName() != PieceType.ROOK  &&
                    board.getCellobj()[i][y].getPieceObj().getName() != PieceType.QUEEN ))
                break ;
        }

        //up
        for(int i=x-1 ; i>=0 ; i--)
        {
            if(board.getCellobj()[i][y].getPieceObj()==null)
                continue;

            if(board.getCellobj()[i][y].getPieceObj().getIsWhite()!=kingColor &&
                    (board.getCellobj()[i][y].getPieceObj().getName() == PieceType.ROOK ||
                            board.getCellobj()[i][y].getPieceObj().getName() == PieceType.QUEEN ))
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[i][y]) ;

                return true ;
            }

            if(board.getCellobj()[i][y].getPieceObj().getIsWhite()==kingColor)
                break ;

            if((board.getCellobj()[i][y].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[i][y].getPieceObj().getName() != PieceType.ROOK  &&
                    board.getCellobj()[i][y].getPieceObj().getName() != PieceType.QUEEN ))
                break ;
        }

        //right
        for(int i=y+1 ; i<8 ; i++)
        {
            if(board.getCellobj()[x][i].getPieceObj()==null)
                continue;

            if(board.getCellobj()[x][i].getPieceObj().getIsWhite()!=kingColor &&
                    (board.getCellobj()[x][i].getPieceObj().getName() == PieceType.ROOK ||
                            board.getCellobj()[x][i].getPieceObj().getName() == PieceType.QUEEN ))
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[x][i]) ;

                return true ;
            }

            if(board.getCellobj()[x][i].getPieceObj().getIsWhite()==kingColor)
                break ;

            if((board.getCellobj()[x][i].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[x][i].getPieceObj().getName() != PieceType.ROOK  &&
                    board.getCellobj()[x][i].getPieceObj().getName() != PieceType.QUEEN ))
                break ;
        }

        //left
        for(int i=y-1 ; i>=0 ; i--)
        {
            if(board.getCellobj()[x][i].getPieceObj()==null)
                continue;

            if(board.getCellobj()[x][i].getPieceObj().getIsWhite()!=kingColor &&
                    (board.getCellobj()[x][i].getPieceObj().getName() == PieceType.ROOK ||
                            board.getCellobj()[x][i].getPieceObj().getName() == PieceType.QUEEN ))
            {
                if(isMoveReal == true)
                    board.getKingCheckList()[idx].add(board.getCellobj()[x][i]) ;

                return true ;
            }

            if(board.getCellobj()[x][i].getPieceObj().getIsWhite()==kingColor)
                break ;

            if((board.getCellobj()[x][i].getPieceObj().getIsWhite()!=kingColor &&
                    board.getCellobj()[x][i].getPieceObj().getName() != PieceType.ROOK  &&
                    board.getCellobj()[x][i].getPieceObj().getName() != PieceType.QUEEN ))
                break ;
        }
        return false ;

    }

    private boolean KnightCheck(Board board, Cell kingCell,boolean isMoveReal)
    {
        boolean kingColor = kingCell.getPieceObj().getIsWhite() ;

        int idx=0;
        if(kingColor == true)
            idx=1 ;

        int x = kingCell.getPosObj().getI()-1 ;
        int y = (int)(kingCell.getPosObj().getCh()-'A') ;

        int[] dx = { -2, -1, 1, 2, -2, -1, 1, 2 };
        int[] dy = { -1, -2, -2, -1, 1, 2, 2, 1 };

        for (int i = 0; i < 8; i++)
        {
            int newx = x + dx[i];
            int newy = y + dy[i];

            if(newx>=0 && newx<8 && newy>=0 && newy<8)
            {
                if(board.getCellobj()[newx][newy].getPieceObj()!=null &&
                        board.getCellobj()[newx][newy].getPieceObj().getIsWhite()!=kingColor &&
                        board.getCellobj()[newx][newy].getPieceObj().getName() == PieceType.KNIGHT)
                {
                    if(isMoveReal == true)
                        board.getKingCheckList()[idx].add(board.getCellobj()[newx][newy]) ;

                    return true ;
                }
            }
        }

        return false;
    }

    public boolean CheckWinner(Board board, boolean PlayerColor)
    {
        Cell KingCell ;
        if(PlayerColor==true)
            KingCell = board.getKingInfo()[0] ;
        else
            KingCell = board.getKingInfo()[1] ;

        // int idx = 0 ;
        // if(KingCell.getPieceObj().getIsWhite() == true)
        //     idx=1 ;

        //Check King can move
        boolean isKingCanMove = KingMove(board, KingCell) ;

        if(isKingCanMove == true)
            return false ;

        boolean isPlayerCanMove = PlayerCanMove(board, !PlayerColor) ;

        return !isPlayerCanMove ;

        //it means King Can't Move there check for pieceKill
        // boolean isCheckPieceKill = CheckPieceKill(KingCell.getPieceObj().getIsWhite()) ;

        // if(isCheckPieceKill == true)
        //     return false ;

        // //it means king cant move and cant kill piece therefore findObstacle 
        // boolean isObstacle = false ;
        // PieceType PieceName = board.getKingCheckList()[idx].get(0).getPieceObj().getName() ;

        // if(PieceName == PieceType.QUEEN || PieceName == PieceType.BISHOP || PieceName == PieceType.ROOK)
        //     isObstacle = CheckObstacle(this,KingCell,PlayerColor) ;

        // if(isObstacle == true)
        //     return false ;

        //return true ;
    }

    private boolean PlayerCanMove(Board board, boolean PlayerColor)
    {
        int idx=0 ;
        if(PlayerColor == true)
            idx=1 ;

        Cell KingCell = board.getKingInfo()[idx] ;

        for(int i=0 ; i<8; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                if(board.getCellobj()[i][j].getPieceObj() == null || board.getCellobj()[i][j].getPieceObj().getIsWhite() != PlayerColor)
                    continue ;

                Cell src = board.getCellobj()[i][j] ;
                ArrayList<Cell> dest ;
                PieceType PieceName = src.getPieceObj().getName() ;

                dest = FindDestCell(board, src,PieceName) ;

                for(int k=0 ; k<dest.size() ; k++)
                {
                    if(MakeVirtualMove(board, KingCell, src, dest.get(k), false,src.getPieceObj().getName() == PieceType.KING) == true)
                        return true ;
                }
            }
        }
        return false;
    }

    private ArrayList<Cell> FindDestCell(Board board, Cell src, PieceType pieceName)
    {
        int x = src.getPosObj().getI()-1 ;
        int y = (int)(src.getPosObj().getCh()-'A') ;
        boolean PieceColor = src.getPieceObj().getIsWhite() ;

        ArrayList<Cell> dest = new ArrayList<>() ;

        if(src.getPieceObj().getName() == PieceType.KNIGHT)
        {
            int[] dx = { -2, -1, 1, 2, -2, -1, 1, 2 };
            int[] dy = { -1, -2, -2, -1, 1, 2, 2, 1 };

            for (int i = 0; i < 8; i++)
            {
                int newx = x + dx[i];
                int newy = y + dy[i];

                if(newx>=0 && newx<8 && newy>=0 && newy<8)
                {
                    if(board.getCellobj()[newx][newy].getPieceObj()==null ||
                            board.getCellobj()[newx][newy].getPieceObj().getIsWhite()!=PieceColor)
                    {
                        dest.add(board.getCellobj()[newx][newy]) ;
                    }
                }
            }
        }

        if(src.getPieceObj().getName() == PieceType.ROOK || src.getPieceObj().getName() == PieceType.QUEEN)
        {
            //down
            for(int i=x+1 ; i<8 ; i++)
            {
                if(board.getCellobj()[i][y].getPieceObj()==null)
                    dest.add(board.getCellobj()[i][y]) ;

                else if(board.getCellobj()[i][y].getPieceObj().getIsWhite()!=PieceColor)
                {
                    dest.add(board.getCellobj()[i][y]) ;
                    break ;
                }
                else
                    break ;
            }

            //up
            for(int i=x-1 ; i>=0 ; i--)
            {
                if(board.getCellobj()[i][y].getPieceObj()==null)
                    dest.add(board.getCellobj()[i][y]) ;

                else if(board.getCellobj()[i][y].getPieceObj().getIsWhite()!=PieceColor)
                {
                    dest.add(board.getCellobj()[i][y]) ;
                    break ;
                }
                else
                    break ;
            }

            //right
            for(int i=y+1 ; i<8 ; i++)
            {
                if(board.getCellobj()[x][i].getPieceObj()==null)
                    dest.add(board.getCellobj()[x][i]) ;

                else if(board.getCellobj()[x][i].getPieceObj().getIsWhite()!=PieceColor)
                {
                    dest.add(board.getCellobj()[x][i]) ;
                    break ;
                }
                else
                    break ;
            }

            //left
            for(int i=y-1 ; i>=0 ; i--)
            {
                if(board.getCellobj()[x][i].getPieceObj()==null)
                    dest.add(board.getCellobj()[x][i]) ;

                else if(board.getCellobj()[x][i].getPieceObj().getIsWhite()!=PieceColor)
                {
                    dest.add(board.getCellobj()[x][i]) ;
                    break ;
                }
                else
                    break ;
            }

        }

        if(src.getPieceObj().getName() == PieceType.BISHOP || src.getPieceObj().getName() == PieceType.QUEEN)
        {
            int[] dx = {1,1,-1,-1} ;
            int[] dy = {1,-1,-1,1} ;

            for(int i=0 ; i<4 ; i++)
            {
                int newx = x+dx[i] ;
                int newy = y+dy[i] ;

                while(newx<8 && newx>=0 && newy<8 && newy>=0)
                {
                    if(board.getCellobj()[newx][newy].getPieceObj()==null)
                        dest.add(board.getCellobj()[newx][newy]) ;

                    else if(board.getCellobj()[newx][newy].getPieceObj().getIsWhite()!=PieceColor)
                    {
                        dest.add(board.getCellobj()[newx][newy]) ;
                        break ;
                    }
                    else
                        break ;

                    newx += dx[i] ;
                    newy += dy[i] ;
                }
            }
        }

        if(src.getPieceObj().getName() == PieceType.PAWN)
        {
            if(PieceColor == true)
            {
                if(x-1>=0 && y-1>=0 && board.getCellobj()[x-1][y-1].getPieceObj()!=null && board.getCellobj()[x-1][y-1].getPieceObj().getIsWhite()!=PieceColor)
                    dest.add(board.getCellobj()[x-1][y-1]) ;

                if(x-1>=0 && y+1<8 && board.getCellobj()[x-1][y+1].getPieceObj()!=null && board.getCellobj()[x-1][y+1].getPieceObj().getIsWhite()!=PieceColor)
                    dest.add(board.getCellobj()[x-1][y+1]) ;

                if(x-1>=0 && board.getCellobj()[x-1][y].getPieceObj()==null)
                    dest.add(board.getCellobj()[x-1][y]) ;

                if(((PiecePawn)(src.getPieceObj())).getFirstMove() == true)
                {
                    if(x-1>=0 && x-2>=0 && board.getCellobj()[x-1][y].getPieceObj()==null && board.getCellobj()[x-2][y].getPieceObj()==null)
                        dest.add(board.getCellobj()[x-2][y]) ;
                }
            }
            else
            {
                if(x+1<8 && y-1>=0 && board.getCellobj()[x+1][y-1].getPieceObj()!=null && board.getCellobj()[x+1][y-1].getPieceObj().getIsWhite()!=PieceColor)
                    dest.add(board.getCellobj()[x+1][y-1]) ;

                if(x+1<8 && y+1<8 && board.getCellobj()[x+1][y+1].getPieceObj()!=null && board.getCellobj()[x+1][y+1].getPieceObj().getIsWhite()!=PieceColor)
                    dest.add(board.getCellobj()[x+1][y+1]) ;

                if(x+1<8 && board.getCellobj()[x+1][y].getPieceObj()==null)
                    dest.add(board.getCellobj()[x+1][y]) ;

                if(((PiecePawn)(src.getPieceObj())).getFirstMove() == true)
                {
                    if(x+1<8 && x+2<8 && board.getCellobj()[x+1][y].getPieceObj()==null && board.getCellobj()[x+2][y].getPieceObj()==null)
                        dest.add(board.getCellobj()[x+2][y]) ;
                }
            }
        }
        return dest;
    }


    private boolean KingMove(Board board, Cell KingCell)
    {
        int[] dx = {1,1,1,0,-1,-1,-1,0} ;
        int[] dy = {1,0,-1,-1,-1,0,1,1} ;

        int kingPosX = KingCell.getPosObj().getI()-1 ;
        int kingPosY = (int)(KingCell.getPosObj().getCh()-'A') ;
        boolean kingColor = KingCell.getPieceObj().getIsWhite() ;
        boolean isKingMove = true ;

        for(int i=0 ; i<8 ; i++)
        {
            int newx = kingPosX + dx[i] ;
            int newy = kingPosY + dy[i] ;

            if(newx>=0 && newx<8 && newy>=0 && newy<8)
            {
                if(board.getCellobj()[newx][newy].getPieceObj() == null || board.getCellobj()[newx][newy].getPieceObj().getIsWhite()!=kingColor)
                {
                    Cell src = KingCell ;
                    Cell dest = board.getCellobj()[newx][newy] ;

                    boolean isMovePossible = MakeVirtualMove(board, KingCell,src,dest,false,isKingMove) ;

                    if(isMovePossible == true)
                        return true ;
                }
            }
        }

        return false ;
    }

    private boolean MakeVirtualMove(Board board, Cell KingCell,Cell src_cell, Cell dest_cell,boolean isMoveReal,boolean isKingMove)
    {
        boolean isMovePossible = false ;
        Piece destPiece = dest_cell.getPieceObj() ;
        Piece src_piece = src_cell.getPieceObj() ;
        Cell MoveCell ;
        src_cell.setPieceObj(null);
        dest_cell.setPieceObj(src_piece);

        if(isKingMove == true)
            MoveCell = dest_cell ;
        else
            MoveCell = KingCell ;

        if(FindKingCheck(board, MoveCell,isMoveReal)== false)
            isMovePossible = true ;

        ResetVirtualMove(board, src_cell, dest_cell, destPiece);

        return isMovePossible;
    }


    private boolean isCapture(Cell dest_cell,PlayerMove pobj)
    {
        if(dest_cell.getPieceObj()!=null && dest_cell.getPieceObj().getIsWhite() != pobj.getPlayerColor())
            return true ;

        return false;
    }
}
