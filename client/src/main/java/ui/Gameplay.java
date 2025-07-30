package ui;

import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Gameplay {
    private ServerFacade server;
    private boolean tF;

    public Gameplay(ServerFacade s, boolean tf) {
        this.server = s;
        this.tF = tf;
    }

    public void run(){
        printBoard();
        new Scanner(System.in).nextLine();
    }

    public void printBoard(){
        System.out.print(ERASE_SCREEN);

        String[] row = {" a ", "  b ", " c ", "  d ", " e ", "  f ", " g ", "  h  "};

        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "   ");
        for (int i = 0; i < 8; i++){
            System.out.print(row[i]);
        }
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        System.out.print(RESET_BG_COLOR + "\n");

        String[][] board = {
                {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
                {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
                {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
        };

        if(tF){
            for (int i = 0; i < 8; i++){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(8-i) + " ");
                for (int j = 0; j < 8; j++){
                    String color;

                    if ((i + j) % 2 == 0){
                        color = SET_BG_COLOR_MAGENTA;
                    } else {
                        color = SET_BG_COLOR_DARK_GREEN;
                    }

                    System.out.print(color + board[i][j]);
                }
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(8-i) + " ");
                System.out.print(RESET_BG_COLOR + "\n");
            }
        } else {
            for (int i = 0; i < 8; i++){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(1+i) + " ");
                for (int j = 0; j < 8; j++){
                    String color;

                    if ((i + j) % 2 == 0){
                        color = SET_BG_COLOR_MAGENTA;
                    } else {
                        color = SET_BG_COLOR_DARK_GREEN;
                    }

                    System.out.print(color + board[7-i][j]);
                }
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(1+i) + " ");
                System.out.print(RESET_BG_COLOR + "\n");
            }
        }

        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "   ");
        for (int i = 0; i < 8; i++){
            System.out.print(row[i]);
        }
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        System.out.print(RESET_BG_COLOR + "\n");
    }
}
