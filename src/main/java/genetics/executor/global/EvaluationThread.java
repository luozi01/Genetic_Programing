package genetics.executor.global;

import genetics.chromosome.Chromosome;
import genetics.interfaces.FitnessCalc;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class EvaluationThread implements Runnable {
    private final BlockingQueue<Optional<Chromosome>> tasks;
    private final BlockingQueue<Optional<Chromosome>> results;
    private final FitnessCalc evaluator;

    EvaluationThread(BlockingQueue<Optional<Chromosome>> tasks,
                     BlockingQueue<Optional<Chromosome>> results,
                     FitnessCalc evaluator) {
        this.tasks = tasks;
        this.results = results;
        this.evaluator = evaluator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Optional<Chromosome> solution = tasks.take();
                if (!solution.isPresent()) break;
                if (Double.isNaN(solution.get().fitness))
                    solution.get().fitness = evaluator.calc(solution.get());

                results.put(solution);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
