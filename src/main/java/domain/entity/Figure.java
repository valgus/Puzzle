package domain.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Figure on the board
 */
@Embeddable
public class Figure {
    @Enumerated(EnumType.ORDINAL)
    private Type type;
    @Enumerated(EnumType.ORDINAL)
    private Color color;

    public Figure(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    public Figure() {

    }

    public Type getType() {
        return this.type;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return type.toString() + "\t" + color.toString();
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}