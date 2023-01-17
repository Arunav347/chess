package com.hardik.chess.Dao;


import com.hardik.chess.model.GameInformation;

public interface ChessGameDao {

    GameInformation findGameId (String gameId);

    void insertGame(GameInformation gameInformation);
}
