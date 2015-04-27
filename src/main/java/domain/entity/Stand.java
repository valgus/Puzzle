package domain.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Emulate the player's stand
 */

@Embeddable
public class Stand {
    public final static int SIZE = 7;
    @Embedded
    private Bag bag;
    @Embedded
    @ElementCollection
    private List<Figure> figures;

    public Stand(Bag bag) {
        this.bag = bag;
        this.figures = new ArrayList<Figure>(SIZE);
        takeFigures();
    }

    /**
     * Put a figure on the table
     *
     * @param i the number of figure in the stand
     * @return putted figure, null if the stand is empty
     */
    public Figure putFigure(int i) {
        if (i >= SIZE && i < 0) {
            return null;
        }
        return figures.remove(i);
    }

    /**
     * Get figures from bag while the stand is not full
     *
     * @return true if stand is full (size of figures == SIZE),
     * false otherwise.
     */
    public boolean takeFigures() {
        List<Figure> taken = bag.getFigure(SIZE - figures.size());
        figures.addAll(taken);
        return taken.size() >= SIZE - figures.size();
    }

    /**
     * Swap the whole stand with bag
     *
     * @return true if stand is full (size of figures == SIZE),
     * false otherwise.
     */
    public boolean swapFigures() {
        bag.swap(figures);
        figures.clear();
        return takeFigures();
    }

    /**
     * Swap selected figures
     *
     * @param swapped boolean array when true is for swapping
     * @return true if stand is full (size of figures == SIZE),
     * false otherwise.
     */
    public boolean swapFigures(boolean[] swapped) {
        List<Figure> toSwap = new ArrayList<Figure>(swapped.length);
        for (int i = swapped.length - 1; i >= 0; --i) {
            if (swapped[i]) {
                toSwap.add(figures.remove(i));
            }
        }
        bag.swap(toSwap);
        return takeFigures();
    }

    public void abortMove(List<Figure> figures) {
        this.figures.addAll(figures);
    }

    public List<Figure> getFigures() {
        return new ArrayList<Figure>(figures);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Figure figure : figures) {
            res.append(Color.unicode[figure.getColor().ordinal()]);
            res.append(Type.unicode[figure.getType().ordinal()]);
            res.append(GameBoard.ANSI_RESET);
            res.append('\t');
        }
        res.append("\n");
        return res.toString();
    }
}