package example.tgp;

import genetics.data.DataSet;
import org.eclipse.collections.api.tuple.Pair;
import tgp.gp.TGPChromosome;
import tgp.solver.TGPSolver;

import java.util.concurrent.ExecutionException;

public class SymbolicRegression {

  // x^2+3x+2y+z+5
  private static double testFunction(double x, double y, double z) {
    return x * x + 3 * x + 2 * y + z + 5;
  }

  static DataSet generateDataSet() {
    double lower_bound = -50;
    double upper_bound = 50;
    int period = 16;

    DataSet dataSet = DataSet.builder().numInputs(3).numOutputs(1).numSamples(16 * 16 * 16).build();
    double interval = (upper_bound - lower_bound) / period;

    int index = 0;
    for (int i = 0; i < period; i++) {
      double x = lower_bound + interval * i;
      for (int j = 0; j < period; j++) {
        double y = lower_bound + interval * j;
        for (int k = 0; k < period; k++) {
          double z = lower_bound + interval * k;
          dataSet.setInputData(index, new double[] {x, y, z});
          dataSet.setOutputData(index++, new double[] {testFunction(x, y, z)});
        }
      }
    }
    return dataSet;
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    DataSet dataSet = generateDataSet();
    Pair<DataSet, DataSet> splitDataset = dataSet.split(.8);
    System.out.printf(
        "Training: %d\t Testing: %d\t\n",
        splitDataset.getOne().getNumSamples(), splitDataset.getTwo().getNumSamples());

    TGPSolver solver = new TGPSolver(dataSet.getNumInputs());
    solver.setDataSet(splitDataset.getOne());
    solver.setTargetFitness(5);
    long startTime = System.currentTimeMillis();
    solver.runParallel();
    solver.evolve(1000);
    System.out.printf("Time: %fs\n", (System.currentTimeMillis() - startTime) / 1000.0);
    System.out.printf("RMSE: %f", test(solver, splitDataset.getTwo()));
  }

  private static double test(TGPSolver solver, DataSet testDataSet) {
    final TGPChromosome bestGene = solver.getBestGene();
    double error = 0;
    for (int i = 0; i < testDataSet.getNumSamples(); i++) {
      double predicted = bestGene.eval(testDataSet.getDataSetSampleInputs(i));
      double actual = testDataSet.getDataSetSampleOutput(i, 0);
      error += Math.pow(predicted - actual, 2);
    }
    return Math.sqrt(error / testDataSet.getNumSamples());
  }
}
