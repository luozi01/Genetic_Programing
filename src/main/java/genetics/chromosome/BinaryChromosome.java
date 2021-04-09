package genetics.chromosome;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BinaryChromosome extends IntegerChromosome {
  private static final Random RAND_ENGINE = new Random();

  /**
   * Constructor.
   *
   * @param representation list of {0,1} values representing the chromosome
   */
  public BinaryChromosome(List<Integer> representation) {
    super(representation);
  }

  /**
   * Constructor.
   *
   * @param representation array of {0,1} values representing the chromosome
   */
  public BinaryChromosome(Integer[] representation) {
    super(representation);
  }

  /**
   * Returns a representation of a random binary array of length <code>length</code>.
   *
   * @param length length of the array
   * @return a random binary array of length <code>length</code>
   */
  public static List<Integer> randomBinaryRepresentation(int length) {
    return IntStream.range(0, length)
        .mapToObj(j -> RAND_ENGINE.nextInt(2))
        .collect(Collectors.toList());
  }

  /** {@inheritDoc} */
  @Override
  protected void checkValidity(List<Integer> chromosomeRepresentation) {
    for (int i : chromosomeRepresentation) {
      if (i < 0 || i > 1) {
        throw new IllegalArgumentException(
            String.format("All number should be either 0 or 1, but %s found", i));
      }
    }
  }
}
