package lgp.solver;

import genetics.chromosome.Chromosome;
import genetics.interfaces.FitnessCalc;
import lgp.gp.LGA;
import lgp.gp.LGPChromosome;

public class LGPSolver {
    private final LGA environment;

    public LGPSolver(LinearGP manager, LGPFitnessCalc LGPFitnessCalc) {
        environment = new LGA(new LGPFitness(LGPFitnessCalc), manager);
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

    public double fitness(LGPChromosome chromosome) {
        return chromosome.fitness;
    }

    public interface LGPListener {
        void update(LGPSolver solver);
    }

    private class LGPFitness implements FitnessCalc {
        private final LGPFitnessCalc LGPFitnessCalc;

        LGPFitness(LGPFitnessCalc LGPFitnessCalc) {
            this.LGPFitnessCalc = LGPFitnessCalc;
        }

        @Override
        public double calc(Chromosome chromosome) {
            return LGPFitnessCalc.fitness((LGPChromosome) chromosome);
        }
    }
}
