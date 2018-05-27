package cgp.Solver;

import cgp.gp.CGA;
import cgp.gp.CGPChromosome;
import cgp.gp.CGPGenerator;
import cgp.gp.CGPMutation;
import genetics.Population;
import org.apache.commons.math3.genetics.Chromosome;

public class CGPSolver {
    private final CGA environment;

    public CGPSolver(CartesianGP manager) {
        Population population = new Population(new CGPGenerator(manager));
        environment = new CGA(population, new CGPMutation(manager), manager);
    }

    public void addIterationListener(final CGPListener listener) {
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

    public CGPChromosome getBestGene() {
        return (CGPChromosome) environment.getBest();
    }

    public double fitness(Chromosome chromosome) {
        return chromosome.getFitness();
    }

    public interface CGPListener {
        void update(CGPSolver solver);
    }
}
