package lgp.solver;

import genetics.Population;
import lgp.gp.*;
import org.apache.commons.math3.genetics.Chromosome;

public class LGPSolver {
    private final LGA environment;

    public LGPSolver(LinearGP manager) {
        Population population = new Population(new LGPGenerator(manager));
        environment = new LGA(population, new Crossover(manager),
                new MicroMutation(manager), new MacroMutation(manager), manager);
    }

    public void addIterationListener(final LGPListener listener) {
        environment.addIterationListener(environment ->
                listener.update(this));
    }

    public void evolve(int iteration) {
        environment.evolve(iteration);
    }

    public void evolve() {
        environment.evolve();
    }

    public void terminate() {
        environment.terminate();
    }

    public int getIteration() {
        return environment.getGeneration();
    }

    public LGPChromosome getBestGene() {
        return (LGPChromosome) environment.getBest();
    }

    public double fitness(Chromosome chromosome) {
        return chromosome.getFitness();
    }

    public interface LGPListener {
        void update(LGPSolver solver);
    }
}
