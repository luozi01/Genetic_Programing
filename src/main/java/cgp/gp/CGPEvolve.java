package cgp.gp;

import cgp.interfaces.CGPReproduction;
import cgp.interfaces.CGPSelection;
import cgp.program.Results;
import genetics.common.Population;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Data
@Log4j2
@EqualsAndHashCode(callSuper = true)
public class CGPEvolve extends GeneticAlgorithm<CGPChromosome> {
  private final CGPParams params;
  private CGPReproduction reproduction;

  public CGPEvolve(
      @NonNull Initializer<CGPChromosome> initializer,
      @NonNull CGPSelection selection,
      @NonNull CGPReproduction reproduction,
      @NonNull FitnessCalc<CGPChromosome> fitness,
      @NonNull CGPParams params) {
    super();
    this.setInitializer(initializer);
    this.setPopulation(new Population<>(initializer));
    this.setPopulationSize(this.getPopulation().size());
    this.setSelectionPolicy(selection);
    this.setFitnessCalc(fitness);
    this.params = params;
    this.reproduction = reproduction;

    this.addTerminateListener(
        env -> {
          // check termination conditions
          if (env.getBest().getFitness() <= params.getTargetFitness()) {
            if (params.getUpdateFrequency() != 0) {
              log.info("{}\t{} - Solution Found", env.getGeneration(), this.getBest().getFitness());
            }
            env.terminate();
          }

          // display progress to the user at the update frequency specified
          if (params.getUpdateFrequency() != 0
              && (env.getGeneration() % params.getUpdateFrequency() == 0)) {
            log.info("{}\t{}", env.getGeneration(), this.getBest().getFitness());
          }
        });
  }

  public Results repeatEvolve(
      CGPParams params, int numGens, int numRuns, CGPChromosome... chromosomes)
      throws ExecutionException, InterruptedException {
    int updateFrequency = params.getUpdateFrequency();

    /* set the update frequency so as to to so generational results */
    params.setUpdateFrequency(0);

    Results results = new Results();

    StringBuilder sb = new StringBuilder();
    sb.append("\nRun\tFitness\t\tGenerations\tActive nodes\n");

    /* for each run */
    for (int i = 0; i < numRuns; i++) {
      if (chromosomes.length > 0) {
        this.injectPrevPopulation(chromosomes);
      } else {
        this.initialize();
      }
      this.evolve(numGens);
      this.getBestChromosome().ifPresent(results::add);
      sb.append(
          String.format(
              "%d\t%f\t%d\t\t%d\n",
              i,
              results.getBestCGPChromosomes().get(i).getFitness(),
              results.getBestCGPChromosomes().get(i).getGeneration(),
              results.getBestCGPChromosomes().get(i).getNumActiveNodes()));
    }

    sb.append("----------------------------------------------------\n")
        .append(
            String.format(
                "MEAN\t%f\t%f\t%f\n",
                results.getAverageFitness(),
                results.getAverageGenerations(),
                results.getAverageActiveNodes()))
        .append(
            String.format(
                "MEDIAN\t%f\t%f\t%f\n",
                results.getMedianFitness(),
                results.getMedianGenerations(),
                results.getMedianActiveNodes()))
        .append("----------------------------------------------------\n\n");

    log.info(sb.toString());
    /* restore the original value for the update frequency */
    params.setUpdateFrequency(updateFrequency);

    return results;
  }

  @Override
  public Population<CGPChromosome> evolvePopulation()
      throws ExecutionException, InterruptedException {
    List<CGPChromosome> result = this.getPopulation().getChromosomes();

    /* initialise parent chromosomes */
    List<CGPChromosome> parents = result.subList(0, params.getMu());

    /* initialise children chromosomes */
    List<CGPChromosome> children =
        result.subList(params.getMu(), params.getMu() + params.getLambda());

    /* determine the size of the candidate chromosome based on the evolutionary Strategy */
    int numCandidate;
    if (params.getEvolutionaryStrategy() == '+') {
      numCandidate = params.getMu() + params.getLambda();
    } else if (params.getEvolutionaryStrategy() == ',') {
      numCandidate = params.getLambda();
    } else {
      throw new IllegalArgumentException(
          String.format(
              "The evolutionary strategy '%c' is not known. ", params.getEvolutionaryStrategy()));
    }

    MutableList<CGPChromosome> candidates = Lists.mutable.withInitialCapacity(numCandidate);
    for (int i = 0; i < numCandidate; i++) {
      candidates.add(CGPChromosome.initializeChromosome(params));
    }

    // Set the chromosomes which will be used by the selection scheme
    // dependant upon the evolutionary strategy. i.e. '+' all are used
    // by the selection scheme, ',' only the children are.
    if (params.getEvolutionaryStrategy() == '+') {
      /*
      	Note: the children are placed before the parents to
      	ensure 'new blood' is always selected over old if the
      	fitness are equal.
      */
      for (int i = 0; i < numCandidate; i++) {
        if (i < params.getLambda()) {
          candidates.get(i).copyChromosome(children.get(i));
        } else {
          candidates.get(i).copyChromosome(parents.get(i - params.getLambda()));
        }
      }
    } else if (params.getEvolutionaryStrategy() == ',') {
      for (int i = 0; i < numCandidate; i++) {
        candidates.get(i).copyChromosome(children.get(i));
      }
    }

    /* select the parents from the candidateChromos */
    ((CGPSelection) this.getSelectionPolicy()).select(parents, candidates);

    /* create the children from the parents */
    this.reproduction.reproduce(parents, children);

    Population<CGPChromosome> nextGen = new Population<>();
    for (CGPChromosome parent : parents) {
      nextGen.addChromosome(parent.copy());
    }
    for (CGPChromosome child : children) {
      nextGen.addChromosome(child.copy());
    }
    updateGlobal(getExecutor().evaluate(nextGen));
    this.getBestChromosome().ifPresent(o -> o.setGeneration(this.getGeneration()));

    return nextGen;
  }

  /**
   * Inject user defined chromosome to population
   *
   * @param chromosomes user input chromosome
   */
  public void injectPrevPopulation(CGPChromosome[] chromosomes) {
    MutableList<CGPChromosome> prevPopulation = Lists.mutable.ofAll(Arrays.asList(chromosomes));
    prevPopulation.addAll(this.getPopulation().getChromosomes());
    this.setPopulation(new Population<>(prevPopulation.take(this.getPopulationSize())));
  }
}
