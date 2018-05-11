package treegp.solver;

import ga.utils.RandEngine;
import lombok.Getter;
import lombok.Setter;
import treegp.enums.TGPCrossoverStrategy;
import treegp.enums.TGPInitializationStrategy;
import treegp.enums.TGPMutationStrategy;
import treegp.enums.TGPPopulationReplacementStrategy;
import treegp.program.Operator;
import treegp.program.Type;
import treegp.tools.SimpleRandEngine;

import java.util.*;

@Getter
@Setter
public class TreeGP {
    public TGPInitializationStrategy popInitStrategy = TGPInitializationStrategy.INITIALIZATION_METHOD_FULL;
    public TGPCrossoverStrategy crossoverStrategy = TGPCrossoverStrategy.CROSSOVER_SUBTREE_BIAS;
    public TGPMutationStrategy mutationStrategy = TGPMutationStrategy.MUTATION_SHRINK;
    public TGPPopulationReplacementStrategy replacementStrategy = TGPPopulationReplacementStrategy.MU_PLUS_LAMBDA;

    public RandEngine randEngine = new SimpleRandEngine();
    public int populationSize = 500;
    public int maxDepthForCrossover = 4;
    public int maxProgramDepth = 4;
    public int maxDepthForCreation = 3;
    public double macroMutationRate = 0.75;
    public double microMutationRate = 0.75;
    public double crossoverRate = 0.3;
    public double reproductionRate = 0.0;
    public double elitismRatio = .4;

    private List<Operator> terminal = new ArrayList<>(); // variables | numbers
    private List<Operator> nonTerminal = new ArrayList<>(); // operators
    private Map<String, Double> variables = new HashMap<>();
    private int index;

    public TreeGP(List<Operator> operators, Collection<String> variables) {
        for (Operator op : operators) {
            if (op.argumentCount() == 0)
                terminal.add(op);
            else
                nonTerminal.add(op);
        }
        if (terminal.isEmpty())
            throw new IllegalArgumentException("At least one terminal function must be defined");
        if (variables.isEmpty())
            throw new IllegalArgumentException("At least one variable name must be defined");

        for (String variable : variables) {
            setVariable(variable, 0);
        }
    }

    public double lookupVariable(String variable) {
        return variables.get(variable);
    }

    public void setVariable(String variable, double value) {
        variables.put(variable, value);
    }

    public String getRandomVar() {
        int index = randEngine.nextInt(variables.keySet().size());
        int i = 0;
        for (String varName : variables.keySet()) {
            if (i == index) {
                return varName;
            }
            ++i;
        }
        // Unreachable code
        return null;
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
        return terminal.get(randEngine.nextInt(terminal.size()));
    }

    public Operator getRandomOperator(RandEngine randEngine) {
        double r = randEngine.uniform();

        if (r < 0.3333) {
            return Type.CONSTANT;
        } else if (r < 0.6666 && variables.size() > 0) {
            return Type.VARIABLE;
        } else {
            return getRandomNonTerminal();
        }
    }

    public Operator anyOperatorWithArityLessThan(int maxArity, RandEngine randEngine) {
        List<Operator> ops = new ArrayList<>();
        Type[] entities = Type.values();
        for (Type op : entities) {
            if (op.argumentCount() != 0 && op.argumentCount() < maxArity) {
                ops.add(op);
            }
        }
        return ops.isEmpty() ? null : ops.get(randEngine.nextInt(ops.size()));
    }

    public double getRandomValue() {
        return randEngine.uniform();
    }
}
