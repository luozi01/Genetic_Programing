package lgp.gp;


import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.CrossoverPolicy;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.MutationPolicy;
import genetics.interfaces.SelectionPolicy;
import genetics.selection.TournamentSelection;
import genetics.utils.RandEngine;
import lgp.solver.LinearGP;
import org.eclipse.collections.api.tuple.Pair;

public class LGA extends GeneticAlgorithm {

    private final CrossoverPolicy crossoverPolicy;
    private final MutationPolicy micro, macro;
    private final SelectionPolicy selectionPolicy;
    private final LinearGP manager;

    public LGA(final FitnessCalc fitnessCalc, final LinearGP manager) {
        super(new LGPInitialization(manager), fitnessCalc);
        this.crossoverPolicy = new Crossover(manager);
        this.micro = new MicroMutation(manager);
        this.macro = new MacroMutation(manager);
        this.selectionPolicy = new TournamentSelection();
        this.manager = manager;
    }

    //Todo fix
    @Override
    protected Population evolvePopulation() {
        Population nextGeneration = new Population(population.getChromosomes());
        RandEngine randEngine = manager.getRandEngine();
        final int iPopSize = manager.getPopulationSize();
        int program_count = 0;

        int computationBudget = iPopSize * 8;
        int counter = 0;
        while (program_count < iPopSize && counter < computationBudget) {
            Pair<Chromosome, Chromosome> p1 = selectionPolicy.select(nextGeneration, manager.getTournamentSize(), randEngine);
            Pair<Chromosome, Chromosome> p2 = selectionPolicy.select(nextGeneration, manager.getTournamentSize(), randEngine);

            Chromosome tp1 = p1.getOne();
            Chromosome tp2 = p2.getOne();

            double r = randEngine.uniform();
            if (r < manager.getCrossoverRate()) {
                Pair<Chromosome, Chromosome> crossover = crossoverPolicy.crossover(tp1, tp2);
                tp1 = crossover.getOne();
                tp2 = crossover.getTwo();
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                tp1 = macro.mutate(tp1);
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                tp2 = macro.mutate(tp2);
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                tp1 = micro.mutate(tp1);
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                tp2 = micro.mutate(tp2);
            }

            tp1.fitness = fitnessCalc.calc(tp1);
            tp2.fitness = fitnessCalc.calc(tp2);

            if (tp1.betterThan(p1.getTwo()))
                nextGeneration.setChromosome(nextGeneration.indexOf(p1.getTwo()), tp1);
            else
                program_count++;

            if (tp2.betterThan(p2.getTwo()))
                nextGeneration.setChromosome(nextGeneration.indexOf(p2.getTwo()), tp2);
            else
                ++program_count;
            counter++;
        }
        return nextGeneration;
    }
}
