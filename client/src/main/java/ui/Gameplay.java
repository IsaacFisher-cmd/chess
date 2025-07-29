package ui;

import server.ServerFacade;

import java.util.Scanner;

public class Gameplay {
    private ServerFacade server;

    public Gameplay(ServerFacade s) {
        this.server = s;
    }

    public void run(){
        printBoard();
        new Scanner(System.in).nextLine();
    }

    public void printBoard(){

    }
}
