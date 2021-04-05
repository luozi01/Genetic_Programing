package example.ga;

import genetics.chromosome.Chromosome;
import genetics.chromosome.IntegerChromosome;
import genetics.crossover.OrderedCrossover;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initialization;
import genetics.mutation.SwapMutation;
import genetics.selection.TournamentSelection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class TravelSalesmanProblem {
    public static void main(String[] args) {
        try (Stream<Path> paths = Files.walk(Paths.get("problems/tsp"))) {
            paths.filter(Files::isRegularFile)
                    .forEach(o -> {
                        Tour tour = Tour.buildTour(new File(o.toString()));
                        System.out.println(tour);

                        int tournament = (int) Math.ceil(tour.citiesSize * .1);
                        int elitism = (int) Math.ceil(tournament * .6);
                        GeneticAlgorithm<TSPChromosome> ga = new GeneticAlgorithm<>(
                                new TSPInitialization(500, tour),
                                new TSPEvaluation(),
                                new OrderedCrossover<>(), .7,
                                new SwapMutation<>(0.5), 0.02,
                                new TournamentSelection<>(), tournament, elitism);
                        ga.addTerminateListener(env -> {
                            if (env.getBest().getFitness() == tour.bestKnownSolution) {
                                env.terminate();
                            }
                        });
                        long start = System.currentTimeMillis();
                        try {
                            ga.evolve(2000);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        Chromosome best = ga.getBest();
                        System.out.printf("Value: %.2f, Optimal Value: %d, Time: %.2f\n", best.getFitness(), tour.bestKnownSolution, (System.currentTimeMillis() - start) * 1.0 / 1000);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class City {
        final int id;
        final double x, y;
        int[] distance;

        City(int id, double x, double y, int citiesSize) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.distance = new int[citiesSize];
        }

        public final void setDistanceTo(final City city) {
            double dx = this.x - city.x;
            double dy = this.y - city.y;
            int d = (int) Math.round(Math.sqrt(dx * dx + dy * dy));
            distance[city.id - 1] = d;
        }

        int getDistanceTo(final City city) {
            return distance[city.id - 1];
        }
    }

    private static class Tour {
        int citiesSize;
        MutableList<City> cities;
        int bestKnownSolution;

        Tour(int citiesSize, int bestKnownSolution) {
            this.citiesSize = citiesSize;
            this.bestKnownSolution = bestKnownSolution;
            this.cities = Lists.mutable.withInitialCapacity(citiesSize);
        }

        static Tour buildTour(File file) {
            Tour tour = new Tour(0, 0);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (true) {
                    String line = reader.readLine();
                    if (line == null)
                        break;

                    if (line.contains("DIMENSION")) {
                        String[] dimension = line.split(":");
                        tour.citiesSize = Integer.parseInt(dimension[dimension.length - 1].trim());
                    }

                    if (line.contains("BEST_KNOWN")) {
                        String[] score = line.split(":");
                        tour.bestKnownSolution = Integer.parseInt(score[score.length - 1].trim());
                    }

                    if (!line.contains("EOF")) {
                        if (Character.isDigit(line.charAt(0))) {
                            String[] cities = line.split(" ");
                            tour.add(new City(Integer.parseInt(cities[0]),
                                    Double.parseDouble(cities[1]),
                                    Double.parseDouble(cities[2]),
                                    tour.citiesSize));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tour.updateDistances();
            return tour;
        }

        City getCity(final int id) {
            return cities.get(id - 1);
        }

        void add(final City city) {
            cities.add(city);
        }

        void updateDistances() {
            for (final City c : cities)
                for (final City d : cities)
                    if (c != d) {
                        c.setDistanceTo(d);
                        d.setDistanceTo(c);
                    }
        }

        @Override
        public String toString() {
            return "Tour{" +
                    "citiesSize=" + citiesSize +
                    ", bestKnownSolution=" + bestKnownSolution +
                    '}';
        }
    }

    static class TSPChromosome extends IntegerChromosome {

        final Tour tour;

        public TSPChromosome(List<Integer> representation, Tour tour) {
            super(representation);
            this.tour = tour;
        }

        @Override
        protected void checkValidity(List<Integer> chromosomeRepresentation) {
            if (chromosomeRepresentation.stream().distinct().count() != chromosomeRepresentation.size())
                throw new IllegalArgumentException("Cities id should be unique");
            for (int i : chromosomeRepresentation)
                if (i < 1 || i > chromosomeRepresentation.size())
                    throw new IllegalArgumentException(String.format("All number should be between [1, %d], but %s found", chromosomeRepresentation.size(), i));
        }

        @Override
        public TSPChromosome newCopy(List<Integer> representation) {
            return new TSPChromosome(Lists.mutable.ofAll(representation), this.tour);
        }

        @Override
        public String toString() {
            StringBuilder geneString = new StringBuilder();
            for (Integer gene : getRepresentation()) {
                geneString.append(gene).append(",");
            }
            return geneString.toString();
        }
    }

    static class TSPEvaluation implements FitnessCalc<TSPChromosome> {
        @Override
        public double calc(TSPChromosome chromosome) {
            Tour tour = chromosome.tour;
            int fitness = 0;
            for (int i = 0; i < tour.citiesSize - 1; i++) {
                City curr = tour.getCity(chromosome.getRepresentation().get(i));
                City next = tour.getCity(chromosome.getRepresentation().get(i + 1));
                fitness += curr.getDistanceTo(next);
            }
            fitness += tour.getCity(chromosome.getRepresentation().get(tour.citiesSize - 1))
                    .getDistanceTo(tour.getCity(chromosome.getRepresentation().get(0)));
            return fitness;
        }
    }

    static class TSPInitialization implements Initialization<TSPChromosome> {
        private final Tour tour;
        private final int populationSize;

        public TSPInitialization(int populationSize, Tour tour) {
            this.populationSize = populationSize;
            this.tour = tour;
        }

        @Override
        public List<TSPChromosome> generate() {
            MutableList<TSPChromosome> population = Lists.mutable.withInitialCapacity(populationSize);
            for (int i = 0; i < populationSize; i++) {
                MutableList<Integer> child = Lists.mutable.withInitialCapacity(tour.citiesSize);
                for (int j = 1; j <= tour.citiesSize; j++) {
                    child.add(j);
                }
                population.add(new TSPChromosome(child.shuffleThis(), tour));
            }
            return population;
        }
    }
}
