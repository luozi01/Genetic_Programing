package treegp.solver;

import genetics.chromosome.Chromosome;
import genetics.common.FitnessCalc;
import genetics.common.Population;
import treegp.gp.*;

public class TGPSolver {
    private final TGA environment;

    public TGPSolver(TreeGP manager, TGPFitnessCalc TGPFitnessCalc) {
        environment = new TGA(
                new Population(new TGPGenerator(manager)),
                new TGPFitness(TGPFitnessCalc),
                new Crossover(manager),
                new MicroMutation(manager),
                new MacroMutation(manager),
                manager
        );
    }

    public void addIterationListener(final TGPListener listener) {
        environment.addIterationListener(environment ->
                listener.update(this));
    }

    public void evolve(int iteration) {
        environment.evolve(iteration);
    }

    public Population getPopulation() {
        return environment.getPopulation();
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

    public double fitness(TGPChromosome chromosome) {
        return chromosome.fitness;
    }

    public interface TGPListener {
        void update(TGPSolver solver);
    }

    private class TGPFitness implements FitnessCalc {
        private final TGPFitnessCalc TGPFitnessCalc;

        TGPFitness(TGPFitnessCalc TGPFitnessCalc) {
            this.TGPFitnessCalc = TGPFitnessCalc;
        }

        @Override
        public double calc(Chromosome chromosome) {
            return TGPFitnessCalc.fitness((TGPChromosome) chromosome);
        }
    }
}
