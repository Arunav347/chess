package com.hardik.chess.repository;


import com.hardik.chess.model.GameInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChessGameRepository extends JpaRepository<GameInformation, Integer> {

    GameInformation findByGameId(String gameId);

}
