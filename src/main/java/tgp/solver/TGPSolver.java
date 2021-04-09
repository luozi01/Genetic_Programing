package tgp.solver;

import genetics.data.DataSet;
import genetics.driver.GeneticAlgorithm;
import genetics.selection.TournamentSelection;
import lombok.NonNull;
import tgp.crossover.TGPCrossover;
import tgp.fitness.SupervisedLearning;
import tgp.gp.TGPChromosome;
import tgp.gp.TGPParams;
import tgp.initialization.TGPInitializer;
import tgp.mutation.TGPMutation;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class TGPSolver {
  private final GeneticAlgorithm<TGPChromosome> model;
  private final TGPParams params;

  public TGPSolver(final int numInputs) {
    this.params = TGPParams.initialiseParameters(numInputs);

    this.model =
        GeneticAlgorithm.<TGPChromosome>builder()
            .crossoverPolicy(new TGPCrossover(params))
            .initializer(new TGPInitializer(params))
            .mutationPolicy(new TGPMutation(params))
            .fitnessCalc(new SupervisedLearning(params))
            .selectionPolicy(new TournamentSelection<>())
            .uniformRate(params.getCrossoverRate())
            .mutationRate(params.getMutationRate())
            .tournamentSize(params.getTournamentSize())
            .elitism((int) (params.getPopulationSize() * params.getElitismRatio()))
            .build();

    this.model.addTerminateListener(
        environment -> {
          TGPChromosome bestGene = environment.getBest();

          double bestFit = bestGene.getFitness();

          // log to console
          System.out.printf("Generation = %s\t fit = %s\t\n", environment.getGeneration(), bestFit);

          // halt condition
          if (bestFit < this.params.getTargetFitness()) {
            environment.terminate();
            System.out.printf("Function: %s\n", bestGene);
          }
        });
  }

  public void evolve() throws ExecutionException, InterruptedException {
    this.model.evolve();
  }

  public void evolve(int iteration) throws ExecutionException, InterruptedException {
    this.model.evolve(iteration);
  }

  public void runParallel() {
    this.model.runInGlobal();
  }

  public void setTargetFitness(double fitness) {
    this.params.setTargetFitness(fitness);
  }

  public void setDataSet(@NonNull DataSet dataSet) {
    this.params.setData(Optional.of(dataSet));
  }

  public TGPChromosome getBestGene() {
    return this.model.getBest();
  }
}
