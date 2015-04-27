package domain.entity;

public enum Type {
    TRIANGLE,
    CIRCLE,
    STAR,
    CROSS,
    MOON,
    HEART;

    public static final int size = Type.values().length;
    public static final String[] unicode = new String[] {
            "\u25B3",
            "\u25CB",
            "\u2606",
            "\u2716",
            "\u263E",
            "\u2764"
    };
}
