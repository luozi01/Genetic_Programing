package lgp.solver;

import genetics.Chromosome;
import genetics.FitnessCalc;
import genetics.Population;
import lgp.gp.*;

public class LGPSolver {
    private final LGA environment;
    private LGPFitnessCalc LGPFitnessCalc;

    public LGPSolver(LinearGP manager, LGPFitnessCalc LGPFitnessCalc) {
        this.LGPFitnessCalc = LGPFitnessCalc;
        LGPFitness LGPFitness = new LGPFitness(LGPFitnessCalc);
        Population population = new Population(new LGPGenerator(manager));
        environment = new LGA(population,
                LGPFitness,
                new Crossover(manager),
                new MicroMutation(manager),
                new MacroMutation(manager),
                manager);
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
        private LGPFitnessCalc LGPFitnessCalc;

        LGPFitness(LGPFitnessCalc LGPFitnessCalc) {
            this.LGPFitnessCalc = LGPFitnessCalc;
        }

        @Override
        public double calc(Chromosome chromosome) {
            return LGPFitnessCalc.fitness((LGPChromosome) chromosome);
        }
    }
}
