package treegp.solver;

import ga.Fitness;
import ga.Population;
import treegp.gp.TGA;
import treegp.gp.TGPChromosome;
import treegp.gp.TGPGenerator;

public class TGPSolver {
    private TGA<TGPChromosome> environment;
    private FitnessCalc fitnessCalc;

    public TGPSolver(TreeGP manager, FitnessCalc fitnessCalc) {
        this.fitnessCalc = fitnessCalc;
        TGPFitness TGPFitness = new TGPFitness(fitnessCalc);
        Population<TGPChromosome> population = new Population<>(new TGPGenerator(manager));
        environment = new TGA<>(population, TGPFitness);
        environment.setManager(manager);
    }

    public void addIterationListener(final TGPListener listener) {
        environment.addIterationListener(environment ->
                listener.update(this));
    }

    public void evolve(int iteration) {
        environment.evolve(iteration);
    }

    public void terminate() {
        environment.terminate();
    }

    public int getIteration() {
        return environment.getGeneration();
    }

    public TGPChromosome getBestGene() {
        return environment.getBest();
    }

    public double fitness(TGPChromosome chromosome) {
        return fitnessCalc.fitness(chromosome);
    }

    public interface TGPListener {
        void update(TGPSolver solver);
    }

    private class TGPFitness implements Fitness<TGPChromosome> {
        private FitnessCalc fitnessCalc;

        TGPFitness(FitnessCalc fitnessCalc) {
            this.fitnessCalc = fitnessCalc;
        }

        @Override
        public double calc(TGPChromosome chromosome) {
            return fitnessCalc.fitness(chromosome);
        }
    }
}
