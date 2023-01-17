package com.hardik.chess.Dao;

import com.hardik.chess.model.GameInformation;
import com.hardik.chess.repository.ChessGameRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ChessGameDaoImpl implements ChessGameDao {

    @Autowired
    private ChessGameRepository chessGameRepository;
    @Override
    public GameInformation findGameId(String gameId) {
        GameInformation gameInformation = chessGameRepository.findByGameId(gameId);

        return gameInformation;



    }

    @Override
    public void insertGame(GameInformation gameInformation) {
        chessGameRepository.save(gameInformation);
    }
}
