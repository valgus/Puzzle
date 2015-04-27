package domain.entity;

public class Move {


    private Figure figure;
    private int x, y;

    public Move(Figure figure, int x, int y) {
        this.x = x;
        this.y = y;
        this.figure = figure;
    }


    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
