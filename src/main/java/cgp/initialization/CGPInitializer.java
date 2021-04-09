package cgp.initialization;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.interfaces.Initializer;
import lombok.AllArgsConstructor;
import org.eclipse.collections.impl.factory.Lists;

import java.util.List;

@AllArgsConstructor
public class CGPInitializer implements Initializer<CGPChromosome> {
  private final CGPParams params;

  @Override
  public List<CGPChromosome> generate() {
    List<CGPChromosome> population =
        Lists.mutable.withInitialCapacity(this.params.getMu() + this.params.getLambda());
    for (int i = 0; i < this.params.getMu() + this.params.getLambda(); i++) {
      population.add(CGPChromosome.initializeChromosome(this.params));
    }
    return population;
  }
}
