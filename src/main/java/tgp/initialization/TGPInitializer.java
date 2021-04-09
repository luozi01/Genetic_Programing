package tgp.initialization;

import genetics.interfaces.Initializer;
import lombok.AllArgsConstructor;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import tgp.enums.TGPInitializationStrategy;
import tgp.gp.TGPChromosome;
import tgp.gp.TGPParams;
import tgp.program.SyntaxTreeUtils;
import tgp.program.TreeNode;

import java.util.List;

@AllArgsConstructor
public class TGPInitializer implements Initializer<TGPChromosome> {
  private final TGPParams params;

  @Override
  public List<TGPChromosome> generate() {
    switch (this.params.getPopInitStrategy()) {
      case INITIALIZATION_METHOD_FULL:
      case INITIALIZATION_METHOD_GROW:
      case INITIALIZATION_METHOD_PTC1:
      case INITIALIZATION_METHOD_RANDOM_BRANCH:
        return initializeNotRamped();
      case INITIALIZATION_METHOD_RAMPED_FULL:
      case INITIALIZATION_METHOD_RAMPED_GROW:
        return initializeRamped();
      case INITIALIZATION_METHOD_RAMPED_HALF_HALF:
        return initializeRampedHalfHalf();
      default:
        throw new IllegalArgumentException(
            "Strategy is not implemented: " + this.params.getPopInitStrategy());
    }
  }

  /**
   * Initialized population based on combination of full and grow described in "A Field Guide to
   * Genetic Programming" 2.2
   *
   * @return population
   */
  private List<TGPChromosome> initializeRampedHalfHalf() {
    final int populationSize = this.params.getPopulationSize();
    int maxDepthForCreation = this.params.getMaxDepthForCreation();
    int partCount = maxDepthForCreation - 1;

    int interval = populationSize / partCount;
    int interval2 = interval / 2;

    MutableList<TGPChromosome> pop = Lists.mutable.withInitialCapacity(populationSize);
    for (int i = 0; i < partCount; i++) {
      for (int j = 0; j < interval2; ++j) {
        TreeNode node =
            SyntaxTreeUtils.createWithDepth(
                params, i + 2, TGPInitializationStrategy.INITIALIZATION_METHOD_GROW);
        pop.add(new TGPChromosome(node));
      }
      for (int j = interval2; j < interval; ++j) {
        TreeNode node =
            SyntaxTreeUtils.createWithDepth(
                params, i + 2, TGPInitializationStrategy.INITIALIZATION_METHOD_FULL);
        pop.add(new TGPChromosome(node));
      }
    }

    int popCount = pop.size();

    for (int i = popCount; i < populationSize; ++i) {
      TreeNode node =
          SyntaxTreeUtils.createWithDepth(
              params, i + 2, TGPInitializationStrategy.INITIALIZATION_METHOD_GROW);
      pop.add(new TGPChromosome(node));
    }
    return pop;
  }

  private List<TGPChromosome> initializeRamped() {
    final int populationSize = this.params.getPopulationSize();
    MutableList<TGPChromosome> pop = Lists.mutable.withInitialCapacity(populationSize);
    int maxDepthForCreation = params.getMaxDepthForCreation();
    int partCount = maxDepthForCreation - 1;

    int interval = populationSize / partCount;

    TGPInitializationStrategy method = params.getPopInitStrategy();
    if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_RAMPED_FULL) {
      method = TGPInitializationStrategy.INITIALIZATION_METHOD_FULL;
    } else if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_RAMPED_GROW) {
      method = TGPInitializationStrategy.INITIALIZATION_METHOD_GROW;
    }

    for (int i = 0; i < partCount; i++) {
      for (int j = 0; j < interval; ++j) {
        TreeNode node = SyntaxTreeUtils.createWithDepth(params, i + 1, method);
        pop.add(new TGPChromosome(node));
      }
    }

    int popCount = pop.size();

    for (int i = popCount; i < populationSize; ++i) {
      TreeNode node = SyntaxTreeUtils.createWithDepth(params, maxDepthForCreation, method);
      pop.add(new TGPChromosome(node));
    }

    return pop;
  }

  private List<TGPChromosome> initializeNotRamped() {
    final int populationSize = this.params.getPopulationSize();
    MutableList<TGPChromosome> pop = Lists.mutable.withInitialCapacity(populationSize);
    for (int i = 0; i < populationSize; i++) {
      TreeNode node =
          SyntaxTreeUtils.createWithDepth(
              params, params.getMaxProgramDepth(), params.getPopInitStrategy());
      pop.add(new TGPChromosome(node));
    }
    return pop;
  }
}
