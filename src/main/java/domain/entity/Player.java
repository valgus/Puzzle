package domain.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Player class which implements his behavior with his stand
 * and the game board
 */
@Entity
@Table(name = "Players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Embedded
    private Stand stand;
    @Column
    private int score;
    @Column
    private int order;
    @Embedded
    @ElementCollection
    private List<Figure> currentMove;

    public Player(Bag bag) {
        this.stand = new Stand(bag);
        currentMove = new ArrayList<Figure>(Stand.SIZE);
    }

    public Player() {
        currentMove = new ArrayList<Figure>(Stand.SIZE);
    }

    /**
     * Put a figure on the table
     *
     * @param i the number of figure in the stand
     * @return putted figure, null if the stand is empty
     */
    public Figure putFigure(int i) {
        Figure fig = stand.putFigure(i);
        currentMove.add(fig);
        return fig;
    }

    /**
     * Swap all figures
     *
     * @return true if stand is full (size of figures == SIZE),
     * false otherwise.
     */
    public boolean swapAllFigures() {
        return this.stand.swapFigures();
    }

    /**
     * Swap selected figures
     *
     * @param selected boolean array when true is for swapping
     * @return true if stand is full (size of figures == SIZE),
     * false otherwise.
     */
    public boolean swapSelectedFigures(boolean[] selected) {
        return this.stand.swapFigures(selected);
    }

    /**
     * Add score
     *
     * @return total score
     */
    public int addPoints(int points) {
        // sum logic
        this.currentMove.clear();
        this.score += points;
        this.stand.takeFigures();
        return score;
    }

    /**
     * Abort move with returning figures to the stand
     */
    public void abortMove() {
        this.stand.abortMove(currentMove);
        this.currentMove.clear();
    }

    public List<Figure> getCurrentMove() {
        return new ArrayList<Figure>(currentMove);
    }

    public List<Figure> getStandFigures() {
        return this.stand.getFigures();
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return stand.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStand(Stand stand) {
        this.stand = stand;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}