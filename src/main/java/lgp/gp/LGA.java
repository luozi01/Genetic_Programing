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

    @Override
    protected Population evolvePopulation() {
        RandEngine randEngine = manager.getRandEngine();
        Population nextGeneration = new Population(population.getChromosomes());
        int iPopSize = manager.getPopulationSize();
        int program_count = 0;

        int computationBudget = iPopSize * 8;
        int counter = 0;
        while (program_count < iPopSize && counter < computationBudget) {

            Pair<Chromosome, Chromosome> gp1 = selectionPolicy.select(nextGeneration, manager.getTournamentSize(), randEngine);
            Pair<Chromosome, Chromosome> gp2 = selectionPolicy.select(nextGeneration, manager.getTournamentSize(), randEngine);

            Chromosome tp1 = gp1.getOne();
            Chromosome tp2 = gp2.getOne();

            double r = randEngine.uniform();
            if (r < manager.getCrossoverRate()) {
                Pair<Chromosome, Chromosome> pair = crossoverPolicy.crossover(tp1, tp2);
                tp1 = pair.getOne();
                tp2 = pair.getTwo();
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

            if (tp1.betterThan(gp1.getTwo()))
                nextGeneration.addChromosome(tp1);
            else
                program_count++;

            if (tp2.betterThan(gp2.getTwo()))
                nextGeneration.addChromosome(tp2);
            else
                program_count++;

            counter++;
        }
        return nextGeneration;
    }
}
