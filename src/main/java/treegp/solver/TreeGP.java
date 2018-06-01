package treegp.solver;

import genetics.utils.Observation;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import lombok.Getter;
import lombok.Setter;
import treegp.enums.TGPCrossoverStrategy;
import treegp.enums.TGPEvolveStrategy;
import treegp.enums.TGPInitializationStrategy;
import treegp.enums.TGPMutationStrategy;
import treegp.program.Operator;
import treegp.program.Type;

import java.util.*;

@Getter
@Setter
public class TreeGP {
    private TGPInitializationStrategy popInitStrategy = TGPInitializationStrategy.INITIALIZATION_METHOD_PTC1;
    private TGPCrossoverStrategy crossoverStrategy = TGPCrossoverStrategy.CROSSOVER_SUBTREE_BIAS;
    private TGPMutationStrategy mutationStrategy = TGPMutationStrategy.MUTATION_SHRINK;
    private TGPEvolveStrategy replacementStrategy = TGPEvolveStrategy.TINY_GP;

    private RandEngine randEngine = new SimpleRandEngine();
    private int populationSize = 500;
    private int maxDepthForCreation = 3;
    private int maxDepthForCrossover = 4;
    private int maxProgramDepth = 4;
    private double macroMutationRate = 0.75;
    private double microMutationRate = 0.75;
    private double crossoverRate = 0.3;
    private double reproductionRate = 0.0;
    private double elitismRatio = .4;
    private int tournamentSize = 3;
    private List<Operator> terminal = new ArrayList<>(); // variables | numbers
    private List<Operator> nonTerminal = new ArrayList<>(); // operators
    private Map<String, Double> variables = new HashMap<>();
    private int index;

    private List<Observation> targets = new LinkedList<>();

    public TreeGP(List<Operator> operators, Collection<String> variables, List<Observation> targets) {
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

        this.targets.addAll(targets);

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
