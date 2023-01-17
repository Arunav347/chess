package com.hardik.chess.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardik.chess.Dao.ChessGameDao;
import com.hardik.chess.dto.BaseResponse;
import com.hardik.chess.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Log4j2
public class CreateGameService {


    @Autowired
    private ChessGameDao createGameDaoImpl;

    @Autowired
    private ObjectMapper objectMapper;

    public BaseResponse gameCreation(String gameId) {
         GameInformation gameInformation = createGameDaoImpl.findGameId(gameId);
         if(gameInformation != null) {
             return new BaseResponse("failure", "Game already exists with gameId: "+gameId);
         }
         log.info("creating game with game ID {}", gameId);

         gameInformation = createGameInformation(gameId);
         createGameDaoImpl.insertGame(gameInformation);

         return new BaseResponse("success", "Game created at the back end");


    }

    private GameInformation createGameInformation(String gameId) {
        GameInformation gameInformation = new GameInformation();
        gameInformation.setGameId(gameId);
        gameInformation.setBoard(initialization());
        return gameInformation;
    }

    private String initialization() {
        Board board = new Board();
        board.setCellobj(new Cell[8][8]);
        board.setKingInfo(new Cell[2]);
        board.setKingCheckList(new ArrayList[2]) ;
        board.getKingCheckList()[0] = new ArrayList<Cell>() ;
        board.getKingCheckList()[1] = new ArrayList<Cell>() ;

        Piece pieceobjnull = null ;

        boolean isWhite = false ;

        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                //Pawn
                if(i==1)
                    board.getCellobj()[i][j] = new Cell(new PiecePawn(isWhite),new Position((char)(j+'A'),i+1)) ;

                else if(i==6)
                    board.getCellobj()[i][j] = new Cell(new PiecePawn(!isWhite),new Position((char)(j+'A'),i+1)) ;

                    //Rook
                else if(i==0 && (j==0 || j==7))
                    board.getCellobj()[i][j] = new Cell(new PieceRook(isWhite),new Position((char)(j+'A'),i+1)) ;

                else if(i==7 && (j==0 || j==7))
                    board.getCellobj()[i][j] = new Cell(new PieceRook(!isWhite),new Position((char)(j+'A'),i+1)) ;

                    //Bishop
                else if(i==0 && (j==2 || j==5))
                    board.getCellobj()[i][j] = new Cell(new PieceBishop(isWhite),new Position((char)(j+'A'),i+1)) ;

                else if(i==7 && (j==2 || j==5))
                    board.getCellobj()[i][j] = new Cell(new PieceBishop(!isWhite),new Position((char)(j+'A'),i+1)) ;

                    //Knight
                else if(i==0 && (j==1 || j==6))
                    board.getCellobj()[i][j] = new Cell(new PieceKnight(isWhite),new Position((char)(j+'A'),i+1)) ;

                else if(i==7 && (j==1 || j==6))
                    board.getCellobj()[i][j] = new Cell(new PieceKnight(!isWhite),new Position((char)(j+'A'),i+1)) ;

                    //King
                else if(i==0 && j==3)
                {
                    board.getCellobj()[i][j] = new Cell(new PieceKing(isWhite),new Position((char)(j+'A'),i+1)) ;
                    board.getKingInfo()[0] =  board.getCellobj()[i][j] ;
                }

                else if(i==7 && j==3)
                {
                    board.getCellobj()[i][j] = new Cell(new PieceKing(!isWhite),new Position((char)(j+'A'),i+1)) ;
                    board.getKingInfo()[1] =  board.getCellobj()[i][j] ;
                }
                //Queen
                else if(i==0 && j==4)
                    board.getCellobj()[i][j] = new Cell(new PieceQueen(isWhite),new Position((char)(j+'A'),i+1)) ;

                else if(i==7 && j==4)
                    board.getCellobj()[i][j] = new Cell(new PieceQueen(!isWhite),new Position((char)(j+'A'),i+1)) ;

                else
                    board.getCellobj()[i][j] = new Cell(pieceobjnull,new Position((char)(j+'A'),i+1)) ;
            }
        }

        try {
            return objectMapper.writeValueAsString(board);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

}
