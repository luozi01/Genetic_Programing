package genetics.chromosome;


import java.util.List;

public abstract class IntegerChromosome extends AbstractListChromosome<Integer> {
    /**
     * Constructor.
     *
     * @param representation list of integer values representing the chromosome
     */
    public IntegerChromosome(List<Integer> representation) {
        super(representation);
    }

    /**
     * Constructor.
     *
     * @param representation array of integer values representing the chromosome
     */
    public IntegerChromosome(Integer[] representation) {
        super(representation);
    }
}
