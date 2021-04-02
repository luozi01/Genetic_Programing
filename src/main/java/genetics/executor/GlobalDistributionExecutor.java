package genetics.executor;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.interfaces.FitnessCalc;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GlobalDistributionExecutor<T extends Chromosome> extends Executor<T> {
    private final ExecutorService executorService;
    private final FitnessCalc<T> fitnessCalc;

    public GlobalDistributionExecutor(FitnessCalc<T> fitnessCalc) {
        this.fitnessCalc = fitnessCalc;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public T evaluate(Population<T> population) {
        Collection<Callable<T>> callable = Lists.mutable.ofInitialCapacity(population.size());
        population.forEach(o -> callable.add(() -> {
            o.setFitness(fitnessCalc.calc(o));
            return o;
        }));
        Optional<T> bestChromosome = Optional.empty();
        try {
            List<Future<T>> futures = executorService.invokeAll(callable);
            double bestFitness = Double.MAX_VALUE;
            for (Future<T> future : futures) {
                T chromosome = future.get();
                if (chromosome.getFitness() < bestFitness) {
                    bestFitness = chromosome.getFitness();
                    bestChromosome = Optional.of(chromosome);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return bestChromosome.orElse(null);
    }

    @Override
    public void shutDown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}