package cgp.gp;

import cgp.interfaces.CGPFitness;
import cgp.interfaces.CGPInitialization;
import cgp.interfaces.CGPReproduction;
import cgp.interfaces.CGPSelection;
import cgp.program.DataSet;
import cgp.program.Results;
import genetics.common.Population;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.CrossoverPolicy;
import genetics.interfaces.MutationPolicy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Setter
@Getter
public class CGPEvolve extends GeneticAlgorithm<CGPChromosome> {
    private final CGPParams params;
    private CGPReproduction reproduction;
    private CGPInitialization initializer;

    public CGPEvolve(CGPInitialization initialization,
                     CrossoverPolicy<CGPChromosome> crossoverPolicy,
                     MutationPolicy<CGPChromosome> mutationPolicy,
                     CGPSelection selection,
                     CGPReproduction reproduction,
                     CGPFitness fitness,
                     CGPParams params) {
        super(initialization, crossoverPolicy, mutationPolicy, selection, fitness);
        this.params = params;
        this.reproduction = reproduction;
        this.initializer = initialization;
    }

    public Results repeatEvolve(CGPParams params,
                                int numGens,
                                int numRuns,
                                CGPChromosome... chromosomes) {
        int updateFrequency = params.getUpdateFrequency();

        /* set the update frequency so as to to so generational results */
        params.setUpdateFrequency(0);

        Results results = new Results();

        StringBuilder sb = new StringBuilder();
        sb.append("\nRun\tFitness\t\tGenerations\tActive nodes\n");

        /* for each run */
        for (int i = 0; i < numRuns; i++) {
            this.setPopulation(new Population<>(chromosomes));
            this.evolve(numGens);
            this.getBestChromosome().ifPresent(results::add);
            sb.append(String.format("%d\t%f\t%d\t\t%d\n", i,
                    results.getBestCGPChromosomes().get(i).getFitness(),
                    results.getBestCGPChromosomes().get(i).getGeneration(),
                    results.getBestCGPChromosomes().get(i).getNumActiveNodes()));
        }

        sb.append("----------------------------------------------------\n")
                .append(String.format("MEAN\t%f\t%f\t%f\n", results.getAverageFitness(), results.getAverageGenerations(), results.getAverageActiveNodes()))
                .append(String.format("MEDIAN\t%f\t%f\t%f\n", results.getMedianFitness(), results.getMedianGenerations(), results.getMedianActiveNodes()))
                .append("----------------------------------------------------\n\n");

        log.info(sb.toString());
        /* restore the original value for the update frequency */
        params.setUpdateFrequency(updateFrequency);

        return results;
    }

    @Override
    public void evolve(int iteration) {
        DataSet data = params.getData().orElse(null);
        int gen;

        /* error checking */
        if (iteration < 0) {
            throw new IllegalArgumentException(String.format("%d generations is invalid. The number of generations must be >= 0.", iteration));
        }

        if (data != null && params.getNumInputs() != data.getNumInputs()) {
            throw new IllegalArgumentException(String.format("The number of inputs specified in the dataSet (%d) does not match the number of inputs specified in the parameters (%d).", data.getNumInputs(), params.getNumInputs()));
        }

        if (data != null && params.getNumOutputs() != data.getNumOutputs()) {
            throw new IllegalArgumentException(String.format("The number of outputs specified in the dataSet (%d) does not match the number of outputs specified in the parameters (%d).", data.getNumOutputs(), params.getNumOutputs()));
        }

        List<CGPChromosome> result = initializer.generate(params, params.getMu(), params.getLambda(), this.getPopulation());

        /* initialise parent chromosomes */
        List<CGPChromosome> parents = result.subList(0, params.getMu());

        /* initialise children chromosomes */
        List<CGPChromosome> children = result.subList(params.getMu(), params.getMu() + params.getLambda());

        /* initialize best chromosome */
        CGPChromosome bestChromosome = CGPChromosome.initializeChromosome(params);

        /* determine the size of the candidate chromosome based on the evolutionary Strategy */
        int numCandidate;
        if (params.getEvolutionaryStrategy() == '+') {
            numCandidate = params.getMu() + params.getLambda();
        } else if (params.getEvolutionaryStrategy() == ',') {
            numCandidate = params.getLambda();
        } else {
            throw new IllegalArgumentException(String.format("The evolutionary strategy '%c' is not known. ", params.getEvolutionaryStrategy()));
        }

        List<CGPChromosome> candidates = initializer.generate(params, numCandidate);

        /* set fitness of the parents */
        for (int i = 0; i < params.getMu(); i++) {
            parents.get(i).updateFitness(((CGPFitness) this.getFitnessCalc()).calc(params, parents.get(i), data));
        }

        /* show the user whats going on */
        if (params.getUpdateFrequency() != 0) {
            log.info("-- Starting CGP --");
            log.info("Gen\tfitness");
        }

        /* for each generation */
        for (gen = 0; gen < iteration; gen++) {

            /* set fitness of the children of the population */
            for (int i = 0; i < params.getLambda(); i++) {
                children.get(i).updateFitness(((CGPFitness) this.getFitnessCalc()).calc(params, children.get(i), data));
            }

            getBestChromosome(parents, children, params.getMu(), params.getLambda(), bestChromosome);

            // check termination conditions
            if (bestChromosome.getFitness() <= params.getTargetFitness()) {
                if (params.getUpdateFrequency() != 0) {
                    log.info("{}\t{} - Solution Found", gen, bestChromosome.getFitness());
                }
                break;
            }

            // display progress to the user at the update frequency specified
            if (params.getUpdateFrequency() != 0 && (gen % params.getUpdateFrequency() == 0 || gen >= iteration - 1)) {
                log.info("{}\t{}", gen, bestChromosome.getFitness());
            }

            // Set the chromosomes which will be used by the selection scheme
            // dependant upon the evolutionary strategy. i.e. '+' all are used
            // by the selection scheme, ',' only the children are.
            if (params.getEvolutionaryStrategy() == '+') {

			/*
				Note: the children are placed before the parents to
				ensure 'new blood' is always selected over old if the
				fitness are equal.
			*/

                for (int i = 0; i < numCandidate; i++) {
                    if (i < params.getLambda()) {
                        candidates.get(i).copyChromosome(children.get(i));
                    } else {
                        candidates.get(i).copyChromosome(parents.get(i - params.getLambda()));
                    }
                }
            } else if (params.getEvolutionaryStrategy() == ',') {
                for (int i = 0; i < numCandidate; i++) {
                    candidates.get(i).copyChromosome(children.get(i));
                }
            }

            /* select the parents from the candidateChromos */
            ((CGPSelection) this.getSelectionPolicy()).select(params, parents, candidates, params.getMu(), numCandidate);

            /* create the children from the parents */
            this.reproduction.reproduce(params, parents, children, params.getMu(), params.getLambda());
        }

        /* copy the best best chromosome */
        bestChromosome.setGeneration(gen);
        /*copyChromosome(chromo, bestChromosome);*/

        updateGlobal(bestChromosome);
    }

    /**
     * returns a pointer to the fittest chromosome in the two arrays of chromosomes
     * <p>
     * loops through parents and then the children in order for the children to always be selected over the parents
     */
    private void getBestChromosome(List<CGPChromosome> parents,
                                   List<CGPChromosome> children,
                                   int numParents,
                                   int numChildren,
                                   CGPChromosome best) {
        CGPChromosome bestChromoSoFar;
        bestChromoSoFar = parents.get(0);
        for (int i = 1; i < numParents; i++) {
            if (parents.get(i).getFitness() <= bestChromoSoFar.getFitness()) {
                bestChromoSoFar = parents.get(i);
            }
        }

        for (int i = 0; i < numChildren; i++) {
            if (children.get(i).getFitness() <= bestChromoSoFar.getFitness()) {
                bestChromoSoFar = children.get(i);
            }
        }

        best.copyChromosome(bestChromoSoFar);
    }
}
