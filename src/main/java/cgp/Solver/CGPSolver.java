package cgp.Solver;

import cgp.gp.CGA;
import cgp.gp.CGPChromosome;
import cgp.gp.CGPGenerator;
import cgp.gp.CGPMutation;
import genetics.Chromosome;
import genetics.Fitness;
import genetics.Population;

public class CGPSolver {
    private final CGA environment;
    private FitnessCalc fitnessCalc;

    public CGPSolver(CartesianGP manager, FitnessCalc fitnessCalc) {
        this.fitnessCalc = fitnessCalc;
        CGPFitness CGPFitness = new CGPFitness(fitnessCalc);
        Population population = new Population(new CGPGenerator(manager));
        environment = new CGA(
                population,
                CGPFitness,
                new CGPMutation(manager),
                manager);
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

    public double fitness(CGPChromosome chromosome) {
        return fitnessCalc.fitness(chromosome);
    }

    public interface CGPListener {
        void update(CGPSolver solver);
    }

    private class CGPFitness implements Fitness {
        private FitnessCalc fitnessCalc;

        CGPFitness(FitnessCalc fitnessCalc) {
            this.fitnessCalc = fitnessCalc;
        }

        @Override
        public double calc(Chromosome chromosome) {
            return fitnessCalc.fitness((CGPChromosome) chromosome);
        }

    }
}
