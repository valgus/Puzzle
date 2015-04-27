package domain.entity;


        import javax.persistence.ElementCollection;
        import javax.persistence.Embeddable;
        import javax.persistence.Embedded;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;

/**
 * Class for BAG with FIGUREs simulating
 */
@Embeddable
public class Bag {
    @Embedded
    @ElementCollection
    private List<Figure> figures;
    private Random rnd;

    /**
     * Generate the bag.
     * The basic set size is Color.size * Type.size
     *
     * @param setSize specify the amount of basic sets
     */
    public Bag(int setSize) {
        figures = new ArrayList<Figure>(setSize * Color.size * Type.size);
        while (setSize-- > 0) {
            for (Color color : Color.values()) {
                for (Type type : Type.values()) {
                    figures.add(new Figure(type, color));
                }
            }
        }
        rnd = new Random();
    }

    public Bag() {
        rnd = new Random();
    }

    /**
     * Getting random figure and remove them from bag.
     *
     * @return figure from bag, null If bag is empty
     */
    public List<Figure> getFigure(int n) {
        List<Figure> res = new ArrayList<Figure>(n);
        while (n > 0 && !figures.isEmpty()) {
            res.add(figures.remove(rnd.nextInt(figures.size())));
            n--;
        }
        return res;
    }

    /**
     * Add figures to bag when player swap his figures
     * Case when player change the whole stand
     *
     * @param figures set of figures
     */
    public void swap(List<Figure> figures) {
        this.figures.addAll(figures);
    }

    public void setFigures(List<Figure> figures) {
        this.figures = figures;
    }

}