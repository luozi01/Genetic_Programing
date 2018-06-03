package treegp.solver;

import genetics.Chromosome;
import genetics.FitnessCalc;
import genetics.Population;
import treegp.gp.*;

public class TGPSolver {
    private final TGA environment;
    private TGPFitnessCalc TGPFitnessCalc;

    public TGPSolver(TreeGP manager, TGPFitnessCalc TGPFitnessCalc) {
        this.TGPFitnessCalc = TGPFitnessCalc;
        TGPFitness TGPFitness = new TGPFitness(TGPFitnessCalc);
        Population population = new Population(new TGPGenerator(manager));
        environment = new TGA(
                population,
                TGPFitness,
                new Crossover(manager),
                new MicroMutation(manager),
                new MacroMutation(manager),
                manager);
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

    public double fitness(TGPChromosome chromosome) {
        return chromosome.fitness;
    }

    public interface TGPListener {
        void update(TGPSolver solver);
    }

    private class TGPFitness implements FitnessCalc {
        private TGPFitnessCalc TGPFitnessCalc;

        TGPFitness(TGPFitnessCalc TGPFitnessCalc) {
            this.TGPFitnessCalc = TGPFitnessCalc;
        }

        @Override
        public double calc(Chromosome chromosome) {
            return TGPFitnessCalc.fitness((TGPChromosome) chromosome);
        }
    }
}
