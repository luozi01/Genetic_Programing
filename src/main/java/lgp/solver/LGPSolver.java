package lgp.solver;

import genetics.Fitness;
import genetics.Population;
import lgp.gp.LGA;
import lgp.gp.LGPChromosome;
import lgp.gp.LGPGenerator;

public class LGPSolver {
    private LGA<LGPChromosome> environment;
    private FitnessCalc fitnessCalc;

    public LGPSolver(LinearGP manager, FitnessCalc fitnessCalc) {
        this.fitnessCalc = fitnessCalc;
        LGPFitness TGPFitness = new LGPFitness(fitnessCalc);
        Population<LGPChromosome> population = new Population<>(new LGPGenerator(manager));
        environment = new LGA<>(population, TGPFitness);
        environment.setManager(manager);
    }

    public void addIterationListener(final LGPListener listener) {
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

    public LGPChromosome getBestGene() {
        return environment.getBest();
    }

    public double fitness(LGPChromosome chromosome) {
        return fitnessCalc.fitness(chromosome);
    }

    public interface LGPListener {
        void update(LGPSolver solver);
    }

    private class LGPFitness implements Fitness<LGPChromosome> {
        private FitnessCalc fitnessCalc;

        LGPFitness(FitnessCalc fitnessCalc) {
            this.fitnessCalc = fitnessCalc;
        }

        @Override
        public double calc(LGPChromosome chromosome) {
            return fitnessCalc.fitness(chromosome);
        }
    }
}
