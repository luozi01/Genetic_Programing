package treegp.solver;

import genetics.Population;
import org.apache.commons.math3.genetics.Chromosome;
import treegp.gp.*;

public class TGPSolver {
    private final TGA environment;

    public TGPSolver(TreeGP manager) {
        Population population = new Population(new TGPGenerator(manager));
        environment = new TGA(population, new Crossover(manager),
                new MicroMutation(manager), new MacroMutation(manager), manager);
    }

    public void addIterationListener(final TGPListener listener) {
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

    public TGPChromosome getBestGene() {
        return (TGPChromosome) environment.getBest();
    }

    public double fitness(Chromosome chromosome) {
        return chromosome.getFitness();
    }

    public interface TGPListener {
        void update(TGPSolver solver);
    }
}
