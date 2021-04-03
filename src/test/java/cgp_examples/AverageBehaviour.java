package cgp_examples;

import cgp.gp.CGPChromosome;
import cgp.program.DataSet;
import cgp.solver.CGPSolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

class AverageBehaviour {

    static Optional<DataSet> initialiseDataSetFromFile(String file) {
        DataSet data = new DataSet();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lineNum = -1;
            String[] dataSet;
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;

                dataSet = line.split(",");
                if (lineNum == -1) {

                    int numInputs = Integer.parseInt(dataSet[0]);
                    int numOutputs = Integer.parseInt(dataSet[1]);
                    int numSamples = Integer.parseInt(dataSet[dataSet.length - 1]);

                    data = new DataSet(numSamples, numInputs, numOutputs);
                } else { //the other lines contain input outputNodes pairs
                    // get the first value on the given line

                    for (int j = 0; j < dataSet.length; j++) {
                        if (j < data.getNumInputs()) {
                            data.setInputData(lineNum, j, Double.parseDouble(dataSet[j]));
                        } else {
                            data.setOutputData(lineNum, j - data.getNumInputs(), Double.parseDouble(dataSet[j]));
                        }
                    }
                }
                // increment the current line index
                lineNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.of(data);
    }

    public static void main(String[] args) {
        int numInputs = 1;
        int numNodes = 15;
        int numOutputs = 1;
        int nodeArity = 2;

        int numGens = 10000;
        int numRuns = 10;

        double targetFitness = 0.1;
        int updateFrequency = 500;

        double averageFitness;

        CGPSolver solver = new CGPSolver(numInputs, numNodes, numOutputs, nodeArity);

        solver.addNodeFunction("add,sub,mul,div,sin");

        solver.setTargetFitness(targetFitness);

        solver.setUpdateFrequency(updateFrequency);

        solver.setData(initialiseDataSetFromFile("problems/cgp/symbolic.data"));

        solver.repeatEvolve(numGens, numRuns);

        averageFitness = solver.getResults().getAverageFitness();

        System.out.printf("The average chromosome fitness is: %f\n", averageFitness);

        CGPChromosome bestGene = solver.getBestGene(true);
        System.out.println(bestGene.getGeneration());
        System.out.println(bestGene.toString(false));
    }
}
