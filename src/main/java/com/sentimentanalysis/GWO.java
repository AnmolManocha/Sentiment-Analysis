package com.sentimentanalysis;

public class GWO {
    private int populationSize;
    private int maxIterations;
    private double[] bestSolution;

    public GWO(int populationSize, int maxIterations) {
        this.populationSize = populationSize;
        this.maxIterations = maxIterations;
        this.bestSolution = null;
    }

    public void optimize(FitnessFunction fitnessFunction) {
        double[][] population = initializePopulation();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            double alpha = 2.0 - (double) iteration / maxIterations; // Alpha parameter for position update

            for (int i = 0; i < populationSize; i++) {
                double[] currentWolf = population[i];
                double[] alphaWolf = population[getAlphaWolfIndex(population, fitnessFunction)];

                for (int j = 0; j < currentWolf.length; j++) {
                    double r1 = Math.random(); // Random coefficient 1
                    double r2 = Math.random(); // Random coefficient 2

                    // Update the position of the current wolf
                    double a = 2.0 * alpha * r1 - alpha; // Coefficient for encircling prey
                    double c = 2.0 * r2; // Coefficient for individual learning

                    double dAlpha = Math.abs(c * alphaWolf[j] - currentWolf[j]);
                    double x1 = alphaWolf[j] - a * dAlpha;

                    double betaWolf = population[getBetaWolfIndex(population, fitnessFunction)][j];
                    double dBeta = Math.abs(c * betaWolf - currentWolf[j]);
                    double x2 = betaWolf - a * dBeta;

                    double deltaWolf = population[getDeltaWolfIndex(population, fitnessFunction)][j];
                    double dDelta = Math.abs(c * deltaWolf - currentWolf[j]);
                    double x3 = deltaWolf - a * dDelta;

                    currentWolf[j] = (x1 + x2 + x3) / 3.0; // Updated position
                }
            }
        }

        // Find the best solution after optimization
        this.bestSolution = population[getAlphaWolfIndex(population, fitnessFunction)];
    }

    private double[][] initializePopulation() {
        double[][] population = new double[populationSize][];

        for (int i = 0; i < populationSize; i++) {
            double[] solution = new double[2];

            // Randomly initialize the solution within the defined range
            solution[0] = Math.random() * 10 + 1; // k parameter range: [1, 11)
            solution[1] = Math.random() * 4 + 1; // m parameter range: [1, 5)

            population[i] = solution;
        }

        return population;
    }

    private int getAlphaWolfIndex(double[][] population, FitnessFunction fitnessFunction) {
        int alphaWolfIndex = 0;
        double alphaFitness = fitnessFunction.calculateFitness(population[0]);

        for (int i = 1; i < populationSize; i++) {
            double fitness = fitnessFunction.calculateFitness(population[i]);
            if (fitness > alphaFitness) {
                alphaFitness = fitness;
                alphaWolfIndex = i;
            }
        }

        return alphaWolfIndex;
    }

    private int getBetaWolfIndex(double[][] population, FitnessFunction fitnessFunction) {
        int betaWolfIndex = 0;
        double betaFitness = fitnessFunction.calculateFitness(population[0]);
        int alphaWolfIndex = getAlphaWolfIndex(population, fitnessFunction);

        for (int i = 1; i < populationSize; i++) {
            if (i != alphaWolfIndex) {
                double fitness = fitnessFunction.calculateFitness(population[i]);
                if (fitness > betaFitness) {
                    betaFitness = fitness;
                    betaWolfIndex = i;
                }
            }
        }

        return betaWolfIndex;
    }

    private int getDeltaWolfIndex(double[][] population, FitnessFunction fitnessFunction) {
        int deltaWolfIndex = 0;
        double deltaFitness = fitnessFunction.calculateFitness(population[0]);
        int alphaWolfIndex = getAlphaWolfIndex(population, fitnessFunction);
        int betaWolfIndex = getBetaWolfIndex(population, fitnessFunction);

        for (int i = 1; i < populationSize; i++) {
            if (i != alphaWolfIndex && i != betaWolfIndex) {
                double fitness = fitnessFunction.calculateFitness(population[i]);
                if (fitness > deltaFitness) {
                    deltaFitness = fitness;
                    deltaWolfIndex = i;
                }
            }
        }

        return deltaWolfIndex;
    }

    public double[] getBestSolution() {
        return bestSolution;
    }

    public interface FitnessFunction {
        double calculateFitness(double[] solution);
    }
}
