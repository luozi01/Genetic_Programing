package tgp.gp;

import static tgp.program.Type.ADD;
import static tgp.program.Type.CONSTANT;
import static tgp.program.Type.DIV;
import static tgp.program.Type.MUL;
import static tgp.program.Type.SUB;
import static tgp.program.Type.VARIABLE;

import genetics.data.DataSet;
import lombok.Data;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import tgp.enums.TGPCrossoverStrategy;
import tgp.enums.TGPInitializationStrategy;
import tgp.enums.TGPMutationStrategy;
import tgp.program.Operator;
import tgp.program.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Data
public class TGPParams {
  private static final Random RANDOM = new Random(System.currentTimeMillis());

  private TGPInitializationStrategy popInitStrategy;
  private TGPCrossoverStrategy crossoverStrategy;
  private TGPMutationStrategy mutationStrategy;

  private int numInputs;
  private int populationSize;
  private int maxDepthForCreation;
  private int maxDepthForCrossover;
  private int maxProgramDepth;
  private double mutationRate;
  private double crossoverRate;
  private double elitismRatio;
  private int tournamentSize;
  private double targetFitness;
  private MutableList<Operator> terminal; // variables | numbers
  private MutableList<Operator> nonTerminal; // operators
  private int index;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private Optional<DataSet> data = Optional.empty();

  public static TGPParams initialiseParameters(int numInputs) {
    TGPParams params = new TGPParams();
    params.popInitStrategy = TGPInitializationStrategy.INITIALIZATION_METHOD_PTC1;
    params.crossoverStrategy = TGPCrossoverStrategy.CROSSOVER_SUBTREE_BIAS;
    params.mutationStrategy = TGPMutationStrategy.MUTATION_SHRINK;

    params.numInputs = numInputs;
    params.populationSize = 500;
    params.maxDepthForCreation = 3;
    params.maxDepthForCrossover = 4;
    params.maxProgramDepth = 4;
    params.mutationRate = 0.75;
    params.crossoverRate = 0.3;
    params.elitismRatio = .1;
    params.tournamentSize = 3;
    params.targetFitness = 0;
    params.terminal = Lists.mutable.of(VARIABLE, CONSTANT); // variables | numbers
    params.nonTerminal = Lists.mutable.of(ADD, SUB, MUL, DIV); // operators

    return params;
  }

  public int getRandomVar() {
    return RANDOM.nextInt(this.numInputs);
  }

  public Operator getRandomNonTerminal() {
    return roundRobinFunctionSelection();
  }

  private Operator roundRobinFunctionSelection() {
    if (index >= nonTerminal.size()) {
      index = 0;
      Collections.shuffle(nonTerminal);
    }
    return nonTerminal.get(index++);
  }

  public Operator getRandomTerminal() {
    return terminal.get(RANDOM.nextInt(terminal.size()));
  }

  public Operator getRandomOperator() {
    double r = RANDOM.nextDouble();

    if (r < 0.3333) {
      return CONSTANT;
    } else if (r < 0.6666 && this.numInputs > 0) {
      return VARIABLE;
    } else {
      return getRandomNonTerminal();
    }
  }

  public Operator anyOperatorWithArityLessThan(int maxArity) {
    List<Operator> ops = new ArrayList<>();
    Type[] entities = Type.values();
    for (Type op : entities) {
      if (op.argumentCount() != 0 && op.argumentCount() < maxArity) {
        ops.add(op);
      }
    }
    return ops.isEmpty() ? null : ops.get(RANDOM.nextInt(ops.size()));
  }

  public double uniform() {
    return RANDOM.nextDouble();
  }

  public int nextInt(int range) {
    return RANDOM.nextInt(range);
  }
}
