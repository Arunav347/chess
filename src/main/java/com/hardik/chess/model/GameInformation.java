package com.hardik.chess.model;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;
import lombok.Generated;
import org.hibernate.annotations.Type;

@Data
@Entity(name = "game_information")
public class GameInformation {

    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;

    @Column(name = "game_id")
    String gameId;

    @Column(name = "board", columnDefinition = "json")
    private String board;


}
