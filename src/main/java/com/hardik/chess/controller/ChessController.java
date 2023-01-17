package com.hardik.chess.controller;

import com.hardik.chess.dto.BaseResponse;
import com.hardik.chess.dto.PlayerMove;
import com.hardik.chess.service.serviceImpl.CreateGameService;
import com.hardik.chess.service.serviceImpl.ValidMoveService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping()
public class ChessController {


    @Autowired
    private CreateGameService createGameService;

    @Autowired
    private ValidMoveService validMoveService;

    @RequestMapping(value = "/move", method = RequestMethod.POST, produces = "application/json")
    public String move( @RequestParam String gameId,
            @RequestBody PlayerMove playerMove) {
            return validMoveService.movePlayer(gameId, playerMove);
    }

    @RequestMapping(value = "/createGame", method = RequestMethod.POST, produces = "application/json")
    public BaseResponse createGame(@RequestHeader String gameId) {

        return createGameService.gameCreation(gameId);

    }

}
