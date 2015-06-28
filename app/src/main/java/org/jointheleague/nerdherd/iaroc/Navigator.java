package org.jointheleague.nerdherd.iaroc;

/**
 * Created by Ruoya on 6/28/15.
 */

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/*
 * Simple stack based navigator for recording a maze path
 */
public class Navigator {
    private Stack<Character> moves;
    private ArrayDeque<Character> returnMoves;
    private Dashboard dashboard;
    private MazeFunctions mazeFunctions;
    private Maze maze;

    public boolean doNextMove(TurnEndHandler turnEndHandler) {
        if (maze.isWallFront()) {
            mazeFunctions.driveBackHalfSquare();
        }
        char nextMove = returnMoves.remove();
        if (nextMove == 'R') {
            dashboard.log("Turning right");
            mazeFunctions.turnRight(turnEndHandler);
            return true;
        } else if (nextMove == 'L') {
            dashboard.log("Turning left");
            mazeFunctions.turnLeft(turnEndHandler);
            return true;
        } else {
            dashboard.log("Going forward");
            mazeFunctions.driveSquare();
            return false;
        }

    }

    private enum Mode {
        Mapping, Backtracking, Solving
    };

    Mode mode = Mode.Mapping;

    public Navigator(Dashboard dashboard,Maze maze, MazeFunctions mazeFunctions) {
        this.dashboard = dashboard;
        this.maze = maze;
        this.mazeFunctions = mazeFunctions;
        moves = new Stack<Character>();
    }

    public void copy() {
         this.returnMoves = new ArrayDeque<>(moves);
    }

    public void recordMove(char move) {
        if (mode == Mode.Solving) return;

        System.out.print((mode == Mode.Mapping?move:Character.toLowerCase(move)));
        System.out.print('-');

        // Record Move
        if (mode == Mode.Mapping) {
            if (move != 'U') {
                moves.push(move);
            } else {
                mode = Mode.Backtracking;
            }
        }


        else if (mode == Mode.Backtracking) {

            // If stack is empty, start over
            if (moves.empty()) {
                switch (move) {
                    case 'R':
                        moves.push('L');
                        break;
                    case 'L':
                        moves.push('R');
                        break;
                    default:
                        System.err.println("This shouldn't have happened!");
                }
                mode = Mode.Mapping;
            }

            // Are we still backtracking
            else {
                Character nextMove = moves.pop();

                if (!isBacktrackMove(move, nextMove)) {
                    switch (move) {
                        case 'N':
                            // Step onto new path (replace a turn with a turn/move)
                            if (nextMove == 'R') {
                                moves.push('L');
                            }
                            else if (nextMove == 'L') {
                                moves.push('R');
                            }
                            break;
                        case 'L':
                            if (nextMove == 'L') {
                                // If we previously took a left and want to take another left, that means straight through
                                moves.push('N');
                            }
                            else if (nextMove == 'N') {
                                // If we previously moved forward, then we need to keep that move and add the turn (remember to reverse it)
                                moves.push('R');
                            }
                            break;
                        case 'R':
                            // Reverse 'L'
                            if (nextMove == 'R') {
                                moves.push('N');
                            }
                            else if (nextMove == 'N') {
                                moves.push('L');
                            }
                            break;
                    }
                    mode = Mode.Mapping;
                }
            }
        }
    }

    private boolean isBacktrackMove(char move, Character nextMove) {
        char next = (nextMove=='R'?'L':(nextMove=='L'?'R':nextMove));  // Switch left and right
        return (move == next);
    }

    public Character getNextMove() {
        if (moves.isEmpty())
            return null;

        char nextMove = moves.pop();

        if (nextMove == 'R') {
            nextMove = 'L';
        } else if (nextMove == 'L') {
            nextMove = 'R';
        }

        return (new Character(nextMove));
    }

    public String returnPath() {
        mode = Mode.Solving;
        System.out.println();
        StringBuilder path = new StringBuilder();
        Character nextMove;
        ;
        while ((nextMove = getNextMove()) != null) {
            path.append(nextMove);
            System.out.print(nextMove);
            System.out.print('-');
        }

        System.out.println();

        return path.toString();
    }
}
