package adam.android.project;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.game.GameState;
import sac.game.GameStateImpl;

public class Checkers extends GameStateImpl implements Runnable{
    private static final int m = 8;
    private static final int n = 8;
    private static final int whiteTile = -1;
    private static final int empty = 0;
    private static final int whitePiece = 1;
    private static final int blackPiece = 2;
    private static final int whiteDame = 3;
    private static final int blackDame = 4;
    public int whitePiecesAmount;
    public int blackPiecesAmount;

    public byte [][] board = null;
    private List<String> possibleCaptures;
    public List<String> bestCaptures;
    private Thread gameThread = null;


    public String winningCommunicate = "";
    private int winPlayer = 0;

    @Override
    public void run() {

    }

    Checkers() {
        board = new byte [m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i <= 2 && (i+j)%2 == 1) {
                    board[i][j] = blackPiece;
                }
                else if (i >= 5 && (i+j)%2 == 1) {
                    board[i][j] = whitePiece;
                }
                else if ((i+j)%2 == 1){
                    board[i][j] = empty;
                }
                else {
                    board[i][j] = whiteTile;
                }
            }
        }
        whitePiecesAmount = 12;
        blackPiecesAmount = 12;
        setMaximizingTurnNow(false);
        gameThread = new Thread(this);
         // Start the thread
         gameThread.start();

     }

    private Checkers(Checkers parent) {
        board = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = parent.board[i][j];
            }
        }
        whitePiecesAmount = parent.whitePiecesAmount;
        blackPiecesAmount = parent.blackPiecesAmount;
        setMaximizingTurnNow(parent.isMaximizingTurnNow());
    }

    /*public static void start() {
        Checkers.setHFunction(new Heuristics());
    }*/

    public List<GameState> generateChildren() {
        List<GameState> children = new ArrayList<GameState>();
        this.checkBoard();
        int piece, dame, dir;
        if (isMaximizingTurnNow()) {
            piece = whitePiece;
            dame = whiteDame;
            dir = -1;
        }
        else {
            piece = blackPiece;
            dame = blackDame;
            dir = 1;
        }

        if (this.bestCaptures.isEmpty()) {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == piece) {
                        if (j - 1 >= 0) {
                            Checkers child = new Checkers(this);
                            if (child.checkMove(i, j, i + dir, j - 1)) {
                                StringBuilder txt = new StringBuilder();
                                txt.append(i);
                                txt.append(j);
                                txt.append(i + dir);
                                txt.append(j - 1);
                                child.setMoveName(txt.toString());
                                children.add(child);
                            }
                        }
                        if (j + 1 < 8) {
                            Checkers child = new Checkers(this);
                            if (child.checkMove(i, j, i + dir, j + 1)) {
                                StringBuilder txt = new StringBuilder();
                                txt.append(i);
                                txt.append(j);
                                txt.append(i + dir);
                                txt.append(j + 1);
                                child.setMoveName(txt.toString());
                                children.add(child);
                            }
                        }
                    }
                    else if (board[i][j] == dame) {
                        for (int k = 1; k < 8; ++k) {
                            for (int l = 1; l < 8; ++l) {
                                Checkers child1 = new Checkers(this);
                                if (child1.checkMove(i, j, i + k, j + l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i + k);
                                    txt.append(j + l);
                                    child1.setMoveName(txt.toString());
                                    children.add(child1);
                                }
                                Checkers child2 = new Checkers(this);
                                if (child2.checkMove(i ,j, i + k, j - l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i + k);
                                    txt.append(j - l);
                                    child2.setMoveName(txt.toString());
                                    children.add(child2);
                                }
                                Checkers child3 = new Checkers(this);
                                if (child3.checkMove(i ,j, i - k, j + l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i - k);
                                    txt.append(j + l);
                                    child3.setMoveName(txt.toString());
                                    children.add(child3);
                                };
                                Checkers child4 = new Checkers(this);
                                if (child4.checkMove(i ,j, i - k, j - l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i - k);
                                    txt.append(j - l);
                                    child4.setMoveName(txt.toString());
                                    children.add(child4);
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            for (int k = 0; k < bestCaptures.size(); ++k) {
                Checkers child = new Checkers(this);
                List<Point> coords = toCoordinates(bestCaptures.get(k));
                if (child.checkMove(coords.get(0).x, coords.get(0).y, coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y)) {
                    StringBuilder txt = new StringBuilder();
                    txt.append(coords.get(0).x);
                    txt.append(coords.get(0).y);
                    txt.append(coords.get(coords.size() - 1).x);
                    txt.append(coords.get(coords.size() - 1).y);
                    child.setMoveName(txt.toString());
                    children.add(child);
                }
            }
        }
        return children;
    }

    private byte [][] cloneBoard(byte [][] board) {
        byte [][] newBoard = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return  newBoard;
    }

    private boolean canMove() {
        int piece;
        int dame;
        if (isMaximizingTurnNow()) {
            piece = whitePiece;
            dame = whiteDame;
        }
        else {
            piece = blackPiece;
            dame = blackDame;
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == dame) {
                    return true;
                }
                else if (board[i][j] == piece) {
                    if (isMaximizingTurnNow()) {
                        try {
                            if (board[i - 1][j + 1] != empty) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            if (board[i - 1][j - 1] != empty) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    } else {
                        try {
                            if (board[i + 1][j + 1] != empty) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            if (board[i + 1][j - 1] != empty) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }
            }
        }
        return false;
    }

    void checkBoard() {
        possibleCaptures = new ArrayList<>();
        bestCaptures = new ArrayList<>();
        byte [][] boardCopy = cloneBoard(board);
        int piece;
        int dame;
        if (isMaximizingTurnNow()) {
            piece = whitePiece;
            dame = whiteDame;
        }
        else {
            piece = blackPiece;
            dame = blackDame;
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (boardCopy[i][j] == piece) {
                    canCapture(cloneBoard(boardCopy), i, j, "");
                }
                else if (boardCopy[i][j] == dame) {
                    dameCanCapture(cloneBoard(boardCopy), i, j, "");
                }
            }
        }
        if (possibleCaptures.isEmpty()) {
            if (!canMove() && isMaximizingTurnNow()) {
                winPlayer = 2;
            }
            else if (!canMove() && !isMaximizingTurnNow()) {
                winPlayer = 1;
            }
        }
        else {
            selectBestCapture();
        }
    }

    private void becameDame(int x, int y) {
        if (isMaximizingTurnNow()) {
            board[x][y] = whiteDame;
            whitePiecesAmount += 2;
        }
        else {
            board[x][y] = blackDame;
            blackPiecesAmount += 2;
        }
    }

    private List<Point> toCoordinates(String capturePath) {
        List<Point> coordinates = new ArrayList<Point>();
        for (int i = 0; i < capturePath.length() - 1; i += 2) {
            Point point = new Point(Character.getNumericValue(capturePath.charAt(i)), Character.getNumericValue(capturePath.charAt(i+1)));
            coordinates.add(point);
        }

        return coordinates;
    }

    private void dameCanCapture(byte [][] board, int x, int y, String capturePath) {
        int oppPiece;
        int oppDame;
        int ownPiece;
        int ownDame;
        if (isMaximizingTurnNow()) {
            oppPiece = blackPiece;
            oppDame = blackDame;
            ownPiece = whitePiece;
            ownDame = whiteDame;
        }
        else {
            oppPiece = whitePiece;
            oppDame = whiteDame;
            ownPiece = blackPiece;
            ownDame = blackDame;
        }

        try {
            for (int i = 1, j = 1; board[x - i][y + j] != ownPiece && board[x - i][y + j] != ownDame; i++, j++) {
                if ((board[x - i][y + j] == oppPiece || board[x - i][y + j] == oppDame) && board[x - (i + 1)][y + (j + 1)] == empty) {
                    for (int k = 1, l = 1; board[x - (i + k)][y + (j + l)] == empty; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y + j, x - (i + k), y + (j + l));
                        dameCanCapture(board, x - (i + k), y + (j + l), capturePath + x + y + (x - i) + (y + j) + (x - (i + k)) + (y + (j + l)));
                    }
                }
                else if ((board[x - i][y + j] == oppPiece || board[x - i][y + j] == oppDame) && (board[x - (i + 1)][y + (j + 1)] == oppPiece || board[x - (i + 1)][y + (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; board[x - i][y - j] != ownPiece && board[x - i][y - j] != ownDame; i++, j++) {
                if ((board[x - i][y - j] == oppPiece || board[x - i][y - j] == oppDame) && board[x - (i + 1)][y - (j + 1)] == empty) {
                    for (int k = 1, l = 1; board[x - (i + k)][y - (j + l)] == empty; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y - j, x - (i + k), y - (j + l));
                        dameCanCapture(board, x - (i + k), y - (j + l), capturePath + x + y + (x - i) + (y - j) + (x - (i + k)) + (y - (j + l)));
                    }
                }
                else if ((board[x - i][y - j] == oppPiece || board[x - i][y - j] == oppDame) && (board[x - (i + 1)][y - (j + 1)] == oppPiece || board[x - (i + 1)][y - (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; board[x + i][y - j] != ownPiece && board[x + i][y - j] != ownDame; i++, j++) {
                if ((board[x + i][y - j] == oppPiece || board[x + i][y - j] == oppDame) && board[x + (i + 1)][y - (j + 1)] == empty) {
                    for (int k = 1, l = 1; board[x + (i + k)][y - (j + l)] == empty; k++, l++) {
                        simDameExecuteCapture(board, x, y, x + i, y - j, x + (i + k), y - (j + l));
                        dameCanCapture(board, x + (i + k), y - (j + l), capturePath + x + y + (x + i) + (y - j) + (x + (i + k)) + (y - (j + l)));
                    }
                }
                else if ((board[x + i][y - j] == oppPiece || board[x + i][y - j] == oppDame) && (board[x + (i + 1)][y - (j + 1)] == oppPiece || board[x + (i + 1)][y - (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; board[x + i][y + j] != ownPiece && board[x + i][y + j] != ownDame; i++, j++) {
                if ((board[x + i][y + j] == oppPiece || board[x + i][y + j] == oppDame) && board[x + (i + 1)][y + (j + 1)] == empty) {
                    for (int k = 1, l = 1; board[x + (i + k)][y + (j + l)] == empty; k++, l++) {
                        simDameExecuteCapture(board, x, y, x + i, y + j, x + (i + k), y + (j + l));
                        dameCanCapture(board, x + (i + k), y + (j + l), capturePath + x + y + (x + i) + (y + j) + (x + (i + k)) + (y + (j + l)));
                    }
                }
                else if ((board[x + i][y + j] == oppPiece || board[x + i][y + j] == oppDame) && (board[x + (i + 1)][y + (j + 1)] == oppPiece || board[x + (i + 1)][y + (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}

        if (!capturePath.equals("")) {
            possibleCaptures.add(capturePath);
        }
    }

    private void canCapture(byte [][] board, int x, int y, String capturePath) {
        int oppPiece;
        int oppDame;
        if (isMaximizingTurnNow()) {
            oppPiece = blackPiece;
            oppDame = blackDame;
        }
        else {
            oppPiece = whitePiece;
            oppDame = whiteDame;
        }

        try {
            if ((board[x - 1][y + 1] == oppPiece || board[x - 1][y + 1] == oppDame) && board[x - 2][y + 2] == empty) {
                simExecuteCapture(board, x, y, x - 1, y + 1, x - 2, y + 2);
                canCapture(board, x - 2, y + 2, capturePath + x + y + (x - 1) + (y + 1) + (x - 2) + (y + 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        try {
            if ((board[x - 1][y - 1] == oppPiece || board[x - 1][y - 1] == oppDame) && board[x - 2][y - 2] == empty) {
                simExecuteCapture(board, x, y, x - 1, y - 1, x - 2, y - 2);
                canCapture(board, x - 2, y - 2, capturePath + x + y + (x - 1) + (y - 1) +(x - 2) + (y - 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        try {
            if ((board[x + 1][y - 1] == oppPiece || board[x + 1][y - 1] == oppDame) && board[x + 2][y - 2] == empty) {
                simExecuteCapture(board, x, y, x + 1, y - 1, x + 2, y - 2);
                canCapture(board, x + 2, y - 2, capturePath + x + y + (x + 1) + (y - 1) + (x + 2) + (y - 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        try {
            if ((board[x + 1][y + 1] == oppPiece || board[x + 1][y + 1] == oppDame) && board[x + 2][y + 2] == empty) {
                simExecuteCapture(board, x, y, x + 1, y + 1, x + 2, y + 2);
                canCapture(board, x + 2, y + 2, capturePath + x + y + (x + 1) + (y + 1) + (x + 2) + (y + 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        if (!capturePath.equals("")) {
            possibleCaptures.add(capturePath);
        }
    }

    private void simDameExecuteCapture(byte [][] board, int x1, int y1, int x2, int y2, int x3, int y3) {
        if (isMaximizingTurnNow()) {
            board[x2][y2] = empty;
            board[x3][y3] = whiteDame;
        }
        else {
            board[x2][y2] = empty;
            board[x3][y3] = blackDame;
        }
        board[x1][y1] = empty;
    }

    private void simExecuteCapture(byte [][] board, int x1, int y1, int x2, int y2, int x3, int y3) {
        if (isMaximizingTurnNow()) {
            board[x2][y2] = empty;
            board[x3][y3] = whitePiece;
        }
        else {
            board[x2][y2] = empty;
            board[x3][y3] = blackPiece;
        }
        board[x1][y1] = empty;
    }

    private void selectBestCapture() {
        if (!possibleCaptures.isEmpty()) {
            int max = 0;
            for (int i = 0; i < possibleCaptures.size(); ++i) {
                if (max < possibleCaptures.get(i).length()) {
                    max = possibleCaptures.get(i).length();
                    bestCaptures.clear();
                    bestCaptures.add(possibleCaptures.get(i));
                } else if (max == possibleCaptures.get(i).length()) {
                    bestCaptures.add(possibleCaptures.get(i));
                }
            }
        }
    }

    public boolean checkMove(int x1, int y1, int x2, int y2) {
        checkBoard();
        try {
            if (!bestCaptures.isEmpty()) {
                for (int i = 0; i < bestCaptures.size(); ++i) {
                    List<Point> coords = toCoordinates(bestCaptures.get(i));
                    if (x1 == coords.get(0).x && y1 == coords.get(0).y) {
                        if (x2 == coords.get(coords.size() - 1).x && y2 == coords.get(coords.size() - 1).y) {
                            executeComplexCapture(coords);
                            return true;
                        }
                    }
                }
            } else {
                if (isMaximizingTurnNow() && board[x1][y1] == whitePiece && board[x2][y2] == empty) {
                    if ((x1 - 1 == x2 && y1 - 1 == y2) || (x1 - 1 == x2 && y1 + 1 == y2)) {
                        makeMove(x1, y1, x2, y2);
                        return true;
                    }
                } else if (!isMaximizingTurnNow() && board[x1][y1] == blackPiece && board[x2][y2] == empty) {
                    if ((x1 + 1 == x2 && y1 - 1 == y2) || (x1 + 1 == x2 && y1 + 1 == y2)) {
                        makeMove(x1, y1, x2, y2);
                        return true;
                    }
                } else if (isMaximizingTurnNow() && board[x1][y1] == whiteDame && board[x2][y2] == empty && Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
                    int xInc, yInc;
                    if (x1 - x2 > 0) {
                        xInc = -1;
                    } else {
                        xInc = 1;
                    }
                    if (y1 - y2 > 0) {
                        yInc = -1;
                    } else {
                        yInc = 1;
                    }
                    int i = xInc;
                    int j = yInc;

                    while ((i + x1) != x2) {
                        if (board[x1 + i][y1 + j] != empty) {
                            return false;
                        }

                        if (xInc > 0) {
                            i++;
                        } else {
                            i--;
                        }
                        if (yInc > 0) {
                            j++;
                        } else {
                            j--;
                        }
                    }
                    makeMove(x1, y1, x2, y2);
                    return true;
                } else if (!isMaximizingTurnNow() && board[x1][y1] == blackDame && board[x2][y2] == empty && Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
                    int xInc, yInc;
                    if (x1 - x2 > 0) {
                        xInc = -1;
                    } else {
                        xInc = 1;
                    }
                    if (y1 - y2 > 0) {
                        yInc = -1;
                    } else {
                        yInc = 1;
                    }
                    int i = xInc;
                    int j = yInc;

                    while ((i + x1) != x2) {
                        if (board[x1 + i][y1 + j] != empty) {
                            return false;
                        }

                        if (xInc > 0) {
                            i++;
                        } else {
                            i--;
                        }
                        if (yInc > 0) {
                            j++;
                        } else {
                            j--;
                        }
                    }
                    makeMove(x1, y1, x2, y2);
                    return true;
                }
                /*else if (!isMaximizingTurnNow() && board[x1][y1] == blackDame && board[x2][y2] == empty && ) {
                    for (int k = 1; k < 8; ++k) {
                        for (int l = 1; l < 8; ++l) {
                            if (board[x1 + k][y1 + l] == empty)
                        }

                }*/
            }
        } catch (ArrayIndexOutOfBoundsException ex) {}

        return false;
    }

    private void makeMove(int x1, int y1, int x2, int y2) {
        byte piece = board[x1][y1];
        board[x1][y1] = empty;
        board[x2][y2] = piece;
        if (isMaximizingTurnNow() && x2 == 0) {
            becameDame(x2, y2);
        }
        else if (!isMaximizingTurnNow() && x2 == 7) {
            becameDame(x2, y2);
        }
        setMaximizingTurnNow(!isMaximizingTurnNow());
    }

    //deprecated
    /*private void makeMove(int x1, int y1, int x2, int y2) {
        board[x1][y1] = empty;
        if (isTurnWhite) {
            board[x2][y2] = whitePiece;
        }
        else {
            board[x2][y2] = blackPiece;
        }
        isTurnWhite = !isTurnWhite;
    }*/

    private void executeComplexCapture(List<Point> coords) {
        byte pieceKind = board[coords.get(0).x][coords.get(0).y];
        board[coords.get(0).x][coords.get(0).y] = empty;

        int x1, y1, x2, y2, x3, y3;
        for (int i = 0; i < coords.size() - 2; i += 3) {
            x1 = coords.get(i).x;
            y1 = coords.get(i).y;
            x2 = coords.get(i + 1).x;
            y2 = coords.get(i + 1).y;
            x3 = coords.get(i + 2).x;
            y3 = coords.get(i + 2).y;

            board[x1][y1] = empty;

            if (board[x2][y2] == whitePiece)
                whitePiecesAmount--;
            else if (board[x2][y2] == whiteDame)
                whitePiecesAmount -= 3;
            else if (board[x2][y2] == blackPiece)
                blackPiecesAmount--;
            else if (board[x2][y2] == blackDame)
                blackPiecesAmount -= 3;

            board[x2][y2] = empty;
            board[x3][y3] = pieceKind;
        }

        if (board[coords.get(coords.size() - 1).x][coords.get(coords.size() - 1).y] == whitePiece || board[coords.get(coords.size() - 1).x][coords.get(coords.size() - 1).y] == blackPiece) {
            if (isMaximizingTurnNow() && coords.get(coords.size() - 1).x == 0) {
                becameDame(coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y);
            } else if (!isMaximizingTurnNow() && coords.get(coords.size() - 1).x == 7) {
                becameDame(coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y);
            }
        }
        setMaximizingTurnNow(!isMaximizingTurnNow());
    }

    // deprecated
        /*private void executeComplexCapture(String capture) {
            board[Character.getNumericValue(capture.charAt(0))][Character.getNumericValue(capture.charAt(1))] = empty;
            for (int i = 0; i < capture.length(); i += 4) {
                int x1, y1, x2, y2;
                x1 = Character.getNumericValue(capture.charAt(i));
                y1 = Character.getNumericValue(capture.charAt(i+1));
                x2 = Character.getNumericValue(capture.charAt(i+2));
                y2 = Character.getNumericValue(capture.charAt(i+3));

                board[x1 - ((x1-x2)/2)][y1 - ((y1-y2)/2)] = empty;
                if (isTurnWhite) {
                    --blackPiecesAmount;
                }
                else {
                    --whitePiecesAmount;
                }
            }
            if (isTurnWhite) {
                board[Character.getNumericValue(capture.charAt(capture.length() - 2))][Character.getNumericValue(capture.charAt(capture.length() - 1))] = whitePiece;
            }
            else {
                board[Character.getNumericValue(capture.charAt(capture.length() - 2))][Character.getNumericValue(capture.charAt(capture.length() - 1))] = blackPiece;
            }
            isTurnWhite = !isTurnWhite;

        }*/

    public void newGame() {
        board = new byte [m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i <= 2 && (i+j)%2 == 1) {
                    board[i][j] = blackPiece;
                }
                else if (i >= 5 && (i+j)%2 == 1) {
                    board[i][j] = whitePiece;
                }
                else if ((i+j)%2 == 1){
                    board[i][j] = empty;
                }
                else {
                    board[i][j] = whiteTile;
                }
            }
        }
        whitePiecesAmount = 12;
        blackPiecesAmount = 12;
        setMaximizingTurnNow(true);
        winPlayer = 0;
    }

    public int hashCode() {
        byte[] lin = new byte[m*n];
        int k = 0;
        for (int i = 0; i<n; i++)
            for (int j = 0; j<m; j++)
                lin[k++] = board[i][j];
        return Arrays.hashCode(lin);
    }

    public boolean isEnd() {
        if (whitePiecesAmount == 0) {
            winningCommunicate = "Black wins!";
            return true;
        }
        else if (blackPiecesAmount == 0) {
            winningCommunicate = "White wins!";
            return true;
        }
        else if (winPlayer == 1) {
            winningCommunicate = "White wins!";
            return true;
        }
        else if (winPlayer == 2) {
            winningCommunicate = "Black wins!";
            return true;
        }
        return false;
    }
}

//          |
//      II  |   I
//          |
//  --------+--------
//          |
//     III  |  IV
//          |

//          |
//    1, 1  |  1, -1
//          |
//  --------+---------
//          |
//   -1, 1  | -1, -1
//          |


