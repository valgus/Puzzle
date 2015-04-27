package domain.entity;

/**
 * Color of figures.
 */
public enum Color {
    RED,
    PINK,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE;

    public static final int size = Color.values().length;
    public static final String[] unicode = new String[] {
            "\u001B[31m",
            "\u001B[35m",
            "\u001B[36m",
            "\u001B[33m",
            "\u001B[32m",
            "\u001B[34m",
    };
}
