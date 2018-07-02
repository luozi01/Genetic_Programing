package cgp.Solver;

import cgp.gp.CGA;
import cgp.gp.CGPChromosome;
import cgp.gp.CGPInitialization;
import cgp.gp.CGPMutation;
import genetics.chromosome.Chromosome;
import genetics.interfaces.FitnessCalc;
import genetics.selection.TournamentSelection;

public class CGPSolver {
    private final CGA environment;

    public CGPSolver(CartesianGP manager, CGPFitnessCalc CGPFitnessCalc) {
        environment = new CGA(
                new CGPInitialization(manager),
                new CGPFitness(CGPFitnessCalc),
                new CGPMutation(manager),
                new TournamentSelection(),
                manager
        );
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
        return chromosome.fitness;
    }

    public interface CGPListener {
        void update(CGPSolver solver);
    }

    private class CGPFitness implements FitnessCalc {
        private final CGPFitnessCalc CGPFitnessCalc;

        CGPFitness(CGPFitnessCalc CGPFitnessCalc) {
            this.CGPFitnessCalc = CGPFitnessCalc;
        }

        @Override
        public double calc(Chromosome chromosome) {
            return CGPFitnessCalc.fitness((CGPChromosome) chromosome);
        }

    }
}
