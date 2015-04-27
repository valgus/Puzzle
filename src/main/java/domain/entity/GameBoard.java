package domain.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// TODO: prize points extraGameBoard
// TODO: should be old figures
@Entity
@Table(name = "Games")
public class GameBoard {
    public static final String ANSI_RESET = "\u001B[0m";
    public final static int SIZE = 5;
    public final static int SET_AMOUNT = 2;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToMany
    @JoinColumn(name = "players")
    private List<Player> players;
    @Embedded
    @ElementCollection
    private Figure[][] gameBoard;
    private int[][] subGameBoard;
    private int committedIndex;
    @Embedded
    private Bag bag;

    public GameBoard(int playersCount) {
        this.gameBoard = new Figure[SIZE][SIZE];
        this.subGameBoard = new int[SIZE][SIZE];
        this.bag = new Bag(SET_AMOUNT * playersCount);
        this.players = new ArrayList<Player>(playersCount);
        for (int i = 0; i < playersCount; ++i) {
            players.add(new Player(bag));
        }
    }

    public GameBoard() {
        this.subGameBoard = new int[SIZE][SIZE];
    }

    /**
     * Put figure on the board
     *
     * @param playerIndex player index which in [1..playersCount]
     * @param figureIndex Order number of a figure in player's stand
     * @param x           row of table [0..SIZE)
     * @param y           column of table [0..SIZE)
     * @return true if player has that figure on the stand and
     * no figure already on the board on the (x, y)
     * false otherwise
     */
    public boolean putFigure(int playerIndex, int figureIndex, int x, int y) {
        if (x >= SIZE || x < 0 || y >= SIZE || y < 0 || gameBoard[x][y] != null
                || playerIndex > players.size()) {
            return false;
        }
        Player player = players.get(playerIndex - 1);
        Figure fig = player.putFigure(figureIndex);
        gameBoard[x][y] = fig;
        // mark uncommitted changes with (- player Index)
        subGameBoard[x][y] = -playerIndex;
        return true;
    }

    /**
     * Commit all changes after all checks on right combinations
     *
     * @param playerIndex index of player
     * @return true if commit succeeded, false overwise
     */
    public boolean commitCurrentMove(int playerIndex) {
        int score;
        if (checkContinuity() && (score = countScore(committedIndex)) != -1) {
            players.get(playerIndex - 1).addPoints(score);
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (subGameBoard[i][j] < 0) {
                        subGameBoard[i][j] *= -1;
                    }
                }
            }
            return true;
        } else {
            abortChanges();
            players.get(playerIndex - 1).abortMove();
            return false;
        }
    }


    // TODO: add price points

    /**
     * Count score walking through the new figures on the board
     *
     * @param index if index > 0 => index = index of row + 1
     *              index < 0 => index = - index of column - 1
     * @return -1 if there are mistakes with combinations
     * score otherwise.
     */
    private int countScore(int index) {
        // 2 ways: major row, minor columns and vise verse
        int[] indexes;
        int score = 0;
        int temp;
        int temp2;
        // index > 0 - row index, < 0 column, 0 error.
        if (index > 0) {
            index--;
            indexes = getRowIndexes(index);
            temp = countCrossRowScore(index, indexes);
            temp2 = checkRow(index, indexes[0]);
        } else {
            index++;
            indexes = getColumnIndexes(-index);
            temp = countCrossColumnScore(-index, indexes);
            temp2 = checkColumn(-index, indexes[0]);
        }
        if (temp < 0 || temp2 < 0) {
            return -1;
        }
        score += temp;
        score += indexes[1] - indexes[0] + 1;
        return score;
    }

    /**
     * Find start and end indexes of a major combination
     *
     * @param row an index of a row where the combination placed
     * @return an array of two values arr[0] - lower bound
     * arr[1] - higher bound
     */
    private int[] getRowIndexes(int row) {
        int first = 0;
        for (int i = 0; i < SIZE; ++i) {
            if (subGameBoard[row][i] < 0) {
                first = i;
                break;
            }
        }
        int second = first;
        while (first >= 0 && subGameBoard[row][first] != 0) {
            first--;
        }
        first++;
        while (second < SIZE && subGameBoard[row][second] != 0) {
            second++;
        }
        second--;
        return new int[]{first, second};
    }

    /**
     * Count a score on the minor combinations
     *
     * @param row     an index of row where the major combination placed
     * @param indexes lower and higher bounds of the combination
     * @return a score, or -1 if combinations are wrong
     */
    private int countCrossRowScore(int row, int[] indexes) {
        int score = 0;
        int temp;
        for (int i = indexes[0]; i <= indexes[1]; ++i) {
            if (subGameBoard[row][i] < 0) {
                temp = checkColumn(row, i);
                if (temp < 0) {
                    return -1;
                }
                score += temp;
            }
        }
        return score;
    }

    /**
     * Check which kind of combination this column is.
     *
     * @param row a row of a uncommitted figure
     * @param col a column of the uncommitted figure
     * @return a score or -1 if combinations are wrong
     */
    private int checkColumn(int row, int col) {
        // detect the type of combination
        boolean combination = true; // false - same color, true - same type
        if (row > 0 && gameBoard[row - 1][col] == null &&
                row < SIZE - 1 && gameBoard[row + 1][col] == null) {
            return 0;
        }
        // if color is not equals => types should be equals
        if (row > 0 &&
                gameBoard[row - 1][col] != null &&
                gameBoard[row - 1][col].getColor() == gameBoard[row][col].getColor() ||
                row < SIZE - 1 &&
                        gameBoard[row + 1][col] != null &&
                        gameBoard[row + 1][col].getColor() == gameBoard[row][col].getColor()) {
            combination = false;
        }
        if (combination) {
            return checkColumnSameType(row, col);
        } else {
            return checkColumnSameColor(row, col);
        }
    }

    /**
     * Check that a combination satisfies condition "Same Type Different Colors"
     *
     * @param row a row of a uncommitted figure
     * @param col a column of the uncommitted figure
     * @return return score or -1 if combinations are wrong
     */
    private int checkColumnSameType(int row, int col) {
        boolean[] checkList = new boolean[Color.size];
        int res = 0;
        checkList[gameBoard[row][col].getColor().ordinal()] = true;
        // go up
        int i = row - 1;
        while (i >= 0 && subGameBoard[i][col] != 0) {
            if (!checkList[gameBoard[i][col].getColor().ordinal()] &&
                    gameBoard[i][col].getType() == gameBoard[row][col].getType()) {
                checkList[gameBoard[i][col].getColor().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i--;
        }
        // go down
        i = row + 1;
        while (i < SIZE && subGameBoard[i][col] != 0) {
            if (!checkList[gameBoard[i][col].getColor().ordinal()] &&
                    gameBoard[i][col].getType() == gameBoard[row][col].getType()) {
                checkList[gameBoard[i][col].getColor().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i++;
        }
        return res;
    }

    /**
     * Check that combination satisfies condition "Different Types Same Color"
     *
     * @param row a row of a uncommitted figure
     * @param col a column of the uncommitted figure
     * @return a score or -1 if combinations are wrong
     */
    private int checkColumnSameColor(int row, int col) {
        boolean[] checkList = new boolean[Type.size];
        int res = 0;
        checkList[gameBoard[row][col].getType().ordinal()] = true;
        //go up
        int i = row - 1;
        while (i >= 0 && subGameBoard[i][col] != 0) {
            if (!checkList[gameBoard[i][col].getType().ordinal()] &&
                    gameBoard[i][col].getColor() == gameBoard[row][col].getColor()) {
                checkList[gameBoard[i][col].getType().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i--;
        }
        // go down
        i = row + 1;
        while (i < SIZE && subGameBoard[i][col] != 0) {
            if (!checkList[gameBoard[i][col].getType().ordinal()] &&
                    gameBoard[i][col].getColor() == gameBoard[row][col].getColor()) {
                checkList[gameBoard[i][col].getType().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i++;
        }
        return res;
    }

    /**
     * Find start and end indexes of the major combination
     *
     * @param col index of a column where the combination placed
     * @return array of two values arr[0] - a lower bound
     * arr[1] - a higher bound
     */
    private int[] getColumnIndexes(int col) {
        int first = 0;
        for (int i = 0; i < SIZE; ++i) {
            if (subGameBoard[i][col] < 0) {
                first = i;
                break;
            }
        }
        int second = first;
        while (first >= 0 && subGameBoard[first][col] != 0) {
            first--;
        }
        first++;
        while (second < SIZE && subGameBoard[second][col] != 0) {
            second++;
        }
        second--;
        return new int[]{first, second};
    }

    /**
     * Count a score on the minor combinations
     *
     * @param col     an index of column where the major combination placed
     * @param indexes lower and higher bounds of the combination
     * @return a score, or -1 if combinations are wrong
     */
    private int countCrossColumnScore(int col, int[] indexes) {
        int score = 0;
        int temp;
        for (int i = indexes[0]; i <= indexes[1]; ++i) {
            if (subGameBoard[i][col] < 0) {
                temp = checkRow(i, col);
                if (temp < 0) {
                    return -1;
                }
                score += temp;
            }
        }
        return score;
    }

    /**
     * Check which kind of combination this row is.
     *
     * @param row a row of a uncommitted figure
     * @param col a column of the uncommitted figure
     * @return a score or -1 if combinations are wrong
     */
    private int checkRow(int row, int col) {
        // detect the type of combination
        boolean combination = true; // false - same color, true - same type
        if (col > 0 && gameBoard[row][col - 1] == null &&
                col < SIZE - 1 && gameBoard[row][col + 1] == null) {
            return 0;
        }
        // if color is not equals => types should be equals
        if (col > 0 &&
                gameBoard[row][col - 1] != null &&
                gameBoard[row][col - 1].getColor() == gameBoard[row][col].getColor() ||
                col < SIZE - 1 &&
                        gameBoard[row][col + 1] != null &&
                        gameBoard[row][col + 1].getColor() == gameBoard[row][col].getColor()) {
            combination = false;
        }
        if (combination) {
            return checkRowSameType(row, col);
        } else {
            return checkRowSameColor(row, col);
        }
    }

    /**
     * Check that a combination satisfies condition "Same Type Different Colors"
     *
     * @param row a row of a uncommitted figure
     * @param col a column of the uncommitted figure
     * @return return score or -1 if combinations are wrong
     */
    private int checkRowSameType(int row, int col) {
        boolean[] checkList = new boolean[Color.size];
        int res = 0;
        checkList[gameBoard[row][col].getColor().ordinal()] = true;
        //go left
        int i = col - 1;
        while (i >= 0 && subGameBoard[row][i] != 0) {
            if (!checkList[gameBoard[row][i].getColor().ordinal()] &&
                    gameBoard[row][i].getType() == gameBoard[row][col].getType()) {
                checkList[gameBoard[row][i].getColor().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i--;
        }
        // go right
        i = col + 1;
        while (i < SIZE && subGameBoard[row][i] != 0) {
            if (!checkList[gameBoard[row][i].getColor().ordinal()] &&
                    gameBoard[row][i].getType() == gameBoard[row][col].getType()) {
                checkList[gameBoard[row][i].getColor().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i++;
        }
        return res;
    }

    /**
     * Check that combination satisfies condition "Different Types Same Color"
     *
     * @param row a row of a uncommitted figure
     * @param col a column of the uncommitted figure
     * @return a score or -1 if combinations are wrong
     */
    private int checkRowSameColor(int row, int col) {
        boolean[] checkList = new boolean[Type.size];
        int res = 0;
        checkList[gameBoard[row][col].getType().ordinal()] = true;
        //go left
        int i = col - 1;
        while (i >= 0 && subGameBoard[row][i] != 0) {
            if (!checkList[gameBoard[row][i].getType().ordinal()] &&
                    gameBoard[row][i].getColor() == gameBoard[row][col].getColor()) {
                checkList[gameBoard[row][i].getType().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i--;
        }
        // go right
        i = col + 1;
        while (i < SIZE && subGameBoard[row][i] != 0) {
            if (!checkList[gameBoard[row][i].getType().ordinal()] &&
                    gameBoard[row][i].getColor() == gameBoard[row][col].getColor()) {
                checkList[gameBoard[row][i].getType().ordinal()] = true;
                res++;
            } else {
                return -1;
            }
            i++;
        }
        return res;
    }

    /**
     * Checks that uncommitted changes are on the one line
     * and not separated by empty spaces
     *
     * @return true if all is OK with placements
     */
    private boolean checkContinuity() {
        if (!checkDiagonal()) {
            return false;
        }
        if (committedIndex > 0) {
            return checkRowContinuity(committedIndex - 1);
        } else
            return committedIndex < 0 && checkColumnContinuity(-committedIndex - 1);
    }

    /**
     * Checks that uncommitted changes are on the one line:
     * horizontal or vertical.
     *
     * @return positive value is an index of row
     * negative value is an minus index of column
     * 0 is not all uncommitted changes are on the one line
     */
    private boolean checkDiagonal() {
        boolean rowComb = true;
        boolean colComb = true;
        int rowIndex = -1;
        int colIndex = -1;
        for (int i = 0; i < SIZE && rowComb; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (subGameBoard[i][j] < 0) {
                    if (rowIndex >= 0 && rowIndex != i) {
                        rowComb = false;
                        break;
                    } else {
                        rowIndex = i;
                    }
                }
            }
        }
        if (rowIndex == -1) {
            rowComb = false;
        }
        for (int i = 0; i < SIZE && colComb; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (subGameBoard[j][i] < 0) {
                    if (colIndex >= 0 && colIndex != i) {
                        colComb = false;
                        break;
                    } else {
                        colIndex = i;
                    }
                }
            }
        }
        if (colIndex == -1) {
            colComb = false;
        }
        if (rowComb && !colComb) {
            committedIndex = rowIndex + 1;
            return true;
        } else if (!rowComb && colComb) {
            committedIndex = -colIndex - 1;
            return true;
        }
        return false;
    }

    /**
     * Checks the continuity of uncommitted changes in a row
     *
     * @param row row index
     * @return true if figures placed continuity
     */
    private boolean checkRowContinuity(int row) {
        int index = 0;
        while (index < SIZE && subGameBoard[row][index] >= 0) {
            index++;
        }
        while (index < SIZE && subGameBoard[row][index] != 0) {
            index++;
        }
        while (index < SIZE && subGameBoard[row][index] != -1) {
            index++;
        }
        return index == SIZE;
    }

    /**
     * Checks the continuity of uncommitted changes in a column
     *
     * @param col column index
     * @return true if figures placed continuity
     */
    private boolean checkColumnContinuity(int col) {
        int index = 0;
        while (index < SIZE && subGameBoard[index][col] >= 0) {
            index++;
        }
        while (index < SIZE && subGameBoard[index][col] != 0) {
            index++;
        }
        while (index < SIZE && subGameBoard[index][col] != -1) {
            index++;
        }
        return index == SIZE;
    }

    /**
     * Abort changes on the game board if something goes wrong
     */
    public void abortChanges() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (subGameBoard[i][j] < 0) {
                    subGameBoard[i][j] = 0;
                    gameBoard[i][j] = null;
                }
            }
        }
    }

    /**
     * Formatting the game board with sub game board to string
     *
     * @return the game board with sub game board in the string to console
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Game Board:\n");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (gameBoard[i][j] != null) {
                    res.append(Color.unicode[gameBoard[i][j].getColor().ordinal()]);
                    res.append(Type.unicode[gameBoard[i][j].getType().ordinal()]);
                    res.append(ANSI_RESET);
                    res.append('\t');
                } else {
                    res.append("\u25A0" + "\t");
                }
            }
            //res.append("\n");
            res.append(Arrays.toString(subGameBoard[i]));
            res.append('\n');
        }
        return res.toString();
    }

    public static void main(String[] args) {
        GameBoard gb = new GameBoard(2);
        Scanner in = new Scanner(System.in);
        Player pl;
        int input = -1;
        int playerNum;
        int figureNum = 0;
        while (input != 0) {
            System.out.println(gb);
            System.out.print("Enter player number: ");
            playerNum = in.nextInt();
            pl = gb.players.get(playerNum - 1);
            while (input != 1) {
                System.out.println(pl);
                System.out.println("Enter \n1-7 to choose figure number\n8 to commit\n9 to swap");
                figureNum = in.nextInt();
                if (figureNum > 7) {
                    break;
                }
                System.out.print("Enter x and y in (1, 15): ");
                gb.putFigure(playerNum, figureNum - 1, in.nextInt() - 1, in.nextInt() - 1);
                System.out.println(gb);
                System.out.println("1 for commit, 2 to add another one");
                input = in.nextInt();
            }
            switch (figureNum) {
                case 0:
                    break;
                case 9:
                    gb.abortChanges();
                    System.out.println("Enter numbers with spaces of figures to swap. Zero to swap all or end input");
                    boolean[] toSwap = new boolean[7];
                    int i;
                    if ((i = in.nextInt()) == 0) {
                        gb.players.get(playerNum - 1).swapAllFigures();
                    } else {
                        do {
                            toSwap[i - 1] = true;
                        } while ((i = in.nextInt()) != 0);
                        gb.players.get(playerNum - 1).swapSelectedFigures(toSwap);
                    }
                    break;
                default:
                    if (gb.commitCurrentMove(playerNum)) {
                        System.out.println("Success");
                        System.out.println("Score Player " + playerNum +
                                " : " + gb.players.get(playerNum - 1).getScore());
                    } else {
                        System.out.println("Abort changes");
                    }
                    break;
            }
            System.out.println("Any key to continue, 0 to exit: ");
            input = in.nextInt();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Figure[][] getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(Figure[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }
}