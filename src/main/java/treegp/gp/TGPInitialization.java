package treegp.gp;

import genetics.chromosome.Chromosome;
import genetics.interfaces.Initialization;
import treegp.enums.TGPInitializationStrategy;
import treegp.program.SyntaxTreeUtils;
import treegp.program.TreeNode;
import treegp.solver.TreeGP;

import java.util.ArrayList;
import java.util.List;

public class TGPInitialization implements Initialization {

    private final TreeGP manager;

    TGPInitialization(TreeGP manager) {
        this.manager = manager;
    }

    @Override
    public List<Chromosome> generate() {
        List<Chromosome> pop = new ArrayList<>();
        TGPInitializationStrategy initializationStrategy = manager.getPopInitStrategy();
        if (initializationStrategy == TGPInitializationStrategy.INITIALIZATION_METHOD_FULL
                || initializationStrategy == TGPInitializationStrategy.INITIALIZATION_METHOD_GROW
                || initializationStrategy == TGPInitializationStrategy.INITIALIZATION_METHOD_PTC1
                || initializationStrategy == TGPInitializationStrategy.INITIALIZATION_METHOD_RANDOM_BRANCH) {
            pop = initializeNotRamped(manager.getPopulationSize());
        } else if (initializationStrategy == TGPInitializationStrategy.INITIALIZATION_METHOD_RAMPED_FULL
                || initializationStrategy == TGPInitializationStrategy.INITIALIZATION_METHOD_RAMPED_GROW) {
            pop = initializeRamped(manager.getPopulationSize());
        } else if (initializationStrategy == TGPInitializationStrategy.INITIALIZATION_METHOD_RAMPED_HALF_HALF) {
            pop = initializeRampedHalfHalf(manager.getPopulationSize());
        }
        return pop;
    }

    private List<Chromosome> initializeRampedHalfHalf(int populationSize) {
        int maxDepthForCreation = manager.getMaxDepthForCreation();
        int part_count = maxDepthForCreation - 1;

        int interval = populationSize / part_count;
        int interval2 = interval / 2;


        List<Chromosome> pop = new ArrayList<>();
        for (int i = 0; i < part_count; i++) {
            for (int j = 0; j < interval2; ++j) {
                TreeNode node = SyntaxTreeUtils.createWithDepth(manager, i + 2,
                        manager, TGPInitializationStrategy.INITIALIZATION_METHOD_GROW);
                pop.add(new TGPChromosome(node, manager));
            }
            for (int j = interval2; j < interval; ++j) {
                TreeNode node = SyntaxTreeUtils.createWithDepth(manager, i + 2,
                        manager, TGPInitializationStrategy.INITIALIZATION_METHOD_FULL);
                pop.add(new TGPChromosome(node, manager));
            }
        }

        int pop_count = pop.size();

        for (int i = pop_count; i < populationSize; ++i) {
            TreeNode node = SyntaxTreeUtils.createWithDepth(manager, i + 2,
                    manager, TGPInitializationStrategy.INITIALIZATION_METHOD_GROW);
            pop.add(new TGPChromosome(node, manager));
        }
        return pop;
    }

    private List<Chromosome> initializeRamped(int populationSize) {
        List<Chromosome> pop = new ArrayList<>();
        int maxDepthForCreation = manager.getMaxDepthForCreation();
        int part_count = maxDepthForCreation - 1;

        int interval = populationSize / part_count;

        TGPInitializationStrategy method = manager.getPopInitStrategy();
        if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_RAMPED_FULL) {
            method = TGPInitializationStrategy.INITIALIZATION_METHOD_FULL;
        } else if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_RAMPED_GROW) {
            method = TGPInitializationStrategy.INITIALIZATION_METHOD_GROW;
        }

        for (int i = 0; i < part_count; i++) {
            for (int j = 0; j < interval; ++j) {
                TreeNode node = SyntaxTreeUtils.createWithDepth(manager, i + 1,
                        manager, method);
                pop.add(new TGPChromosome(node, manager));
            }
        }

        int pop_count = pop.size();

        for (int i = pop_count; i < populationSize; ++i) {
            TreeNode node = SyntaxTreeUtils.createWithDepth(manager, maxDepthForCreation, manager, method);
            pop.add(new TGPChromosome(node, manager));
        }

        return pop;
    }

    private List<Chromosome> initializeNotRamped(int populationSize) {
        List<Chromosome> pop = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            TreeNode node = SyntaxTreeUtils.createWithDepth(manager, manager.getMaxProgramDepth(),
                    manager, manager.getPopInitStrategy());
            pop.add(new TGPChromosome(node, manager));
        }
        return pop;
    }

}
