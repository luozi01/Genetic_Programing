package genetics.chromosome;

import lombok.Data;
import lombok.NonNull;

@Data
public abstract class Chromosome {
  private double fitness = Double.NaN;

  /**
   * @param another chromosome compares to
   * @return if current chromosome has smaller fitness than the compared one
   */
  public boolean betterThan(@NonNull Chromosome another) {
    return Double.compare(fitness, another.fitness) < 0;
  }

  /** @return if chromosome is evaluated */
  public boolean notEvaluated() {
    return Double.isNaN(fitness);
  }
}
