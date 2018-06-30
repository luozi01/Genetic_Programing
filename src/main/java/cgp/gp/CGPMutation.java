package cgp.gp;

import cgp.Solver.CartesianGP;
import cgp.enums.CGPMutationPolicy;
import genetics.chromosome.Chromosome;
import genetics.interfaces.MutationPolicy;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class CGPMutation implements MutationPolicy {

    private final CartesianGP manager;

    public CGPMutation(CartesianGP manager) {
        this.manager = manager;
    }

    @Override
    public Chromosome mutate(Chromosome chromosome) throws MathIllegalArgumentException {
        if (!(chromosome instanceof CGPChromosome)) {
            throw new IllegalArgumentException("Chromosome should be LGPChromosome");
        }

        CGPMutationPolicy policy = manager.getMutationPolicy();
        Chromosome clone;
        switch (policy) {
            case PERCENT_POINT:
                clone = percent_mutate(((CGPChromosome) chromosome).makeCopy());
                break;
            case PROBABILISTIC:
                clone = probabilistic(((CGPChromosome) chromosome).makeCopy());
                break;
            case FIX_POINT:
                clone = fix_point(((CGPChromosome) chromosome).makeCopy());
                break;
            default:
                throw new IllegalStateException("Mutation method is not implemented: " + policy.toString());
        }
        return clone;
    }

    private Chromosome percent_mutate(CGPChromosome chromosome) {
        int totalGenes = (manager.getNodes() * (manager.getArity() + 1)) + manager.getOutput();
        int mutations = (int) Math.floor(.01 * totalGenes);
        for (int i = 0; i < mutations; i++) {
            chromosome.mutate();
        }
        return chromosome;
    }

    private Chromosome fix_point(CGPChromosome chromosome) {
        for (int i = 0; i < manager.getPopulationSize(); i++) {
            chromosome.mutate();
        }
        return chromosome;
    }

    private Chromosome probabilistic(CGPChromosome chromosome) {
        for (int i = 0; i < manager.getNodes(); i++) {
            // go through all connections
            for (int a = 1; a <= manager.getArity(); a++) {
                if (manager.getRandomDouble() < manager.getMutationProbability()) {
                    chromosome.getGenotype()[i][a] = chromosome.getRandomSource(i / manager.getRows());
                }
            }
            if (manager.getRandomDouble() < manager.getMutationProbability()) {
                chromosome.getGenotype()[i][0] = manager.getRandomFunction();
            }
        }
        for (int i = 0; i < manager.getOutput(); i++) {
            if (manager.getRandomDouble() < manager.getMutationProbability()) {
                chromosome.getOutputs()[i] = chromosome.getRandomSource();
            }
        }
        return chromosome;
    }
}
