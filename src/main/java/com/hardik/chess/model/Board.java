package com.hardik.chess.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    private Cell[][] cellobj ;
    private Cell[] kingInfo ;
    private ArrayList<Cell>[] kingCheckList ;

    public Cell getCellInfo(char ch,int j)
    {
        int i = (int)(ch-'A') ;
        return cellobj[j-1][i] ;
    }

    public Cell getCellInfo(int i,int j)
    {
        return cellobj[i][j] ;
    }

}
