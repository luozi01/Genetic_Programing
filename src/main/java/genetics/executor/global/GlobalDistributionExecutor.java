package genetics.executor.global;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.executor.Executor;
import genetics.interfaces.FitnessCalc;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalDistributionExecutor extends Executor {
    private final BlockingQueue<Optional<Chromosome>> tasks;
    private final BlockingQueue<Optional<Chromosome>> results;

    private final int numberOfThreads;
    private final ExecutorService executorService;


    public GlobalDistributionExecutor(int numberOfThreads,
                                      boolean alwaysEval,
                                      BlockingQueue<Optional<Chromosome>> tasks,
                                      BlockingQueue<Optional<Chromosome>> results,
                                      FitnessCalc fitnessCalc) {
        this.tasks = tasks;
        this.results = results;
        this.numberOfThreads = numberOfThreads;
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(new EvaluationThread(tasks, results, fitnessCalc, alwaysEval));
        }
    }

    public void killAll() {
        try {
            int i = 0;
            while (i < numberOfThreads) {
                tasks.put(Optional.empty());
                ++i;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) ;
    }

    @Override
    public Chromosome evaluate(Population population) {
        Optional<Chromosome> bestChromosome = Optional.empty();
        try {
            for (Chromosome solution : population) {
                tasks.put(Optional.of(solution));
            }
            double bestFitness = Double.MAX_VALUE;
            for (int i = 0; i < population.size(); ++i) {
                Optional<Chromosome> chromosome = results.take();
                if (chromosome.isPresent() && chromosome.get().fitness < bestFitness) {
                    bestFitness = chromosome.get().fitness;
                    bestChromosome = chromosome;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bestChromosome.orElse(null);
    }
}