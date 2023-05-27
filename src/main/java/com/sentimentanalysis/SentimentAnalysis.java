package com.sentimentanalysis;

import java.util.*;

public class SentimentAnalysis {
    private List<String[]> trainingSet;
    private List<String[]> testingSet;

    public SentimentAnalysis(List<String[]> trainingSet, List<String[]> testingSet) {
        this.trainingSet = trainingSet;
        this.testingSet = testingSet;
    }

    public void trainModel() {
        double[] optimizedParameters = optimizeParameters();
        FKNN fknn = new FKNN(optimizedParameters);
        fknn.train(trainingSet);
    }

    public void evaluateModel() {
        FKNN fknn = new FKNN();
        int correctPredictions = 0;
        int totalPredictions = 0;

        for (String[] instance : testingSet) {
            String predictedLabel = fknn.classify(instance[0]);
            String trueLabel = instance[1];

            if (predictedLabel.equals(trueLabel)) {
                correctPredictions++;
            }

            totalPredictions++;
        }

        double accuracy = (double) correctPredictions / totalPredictions;
        System.out.println("Accuracy: " + accuracy);
    }

    public void performSentimentAnalysis() {
        trainModel();
        evaluateModel();
    }

    private double[] optimizeParameters() {
        GWO gwo = new GWO(10, 50); // Adjust population size and maximum iterations as needed

        // Define the fitness function for GWO optimization
        GWO.FitnessFunction fitnessFunction = new GWO.FitnessFunction() {
            @Override
            public double calculateFitness(double[] parameters) {
                FKNN fknn = new FKNN(parameters);
                fknn.train(trainingSet);

                int correctPredictions = 0;
                int totalPredictions = 0;

                for (String[] instance : testingSet) {
                    String predictedLabel = fknn.classify(instance[0]);
                    String trueLabel = instance[1];
                    System.out.println("True Label:" + trueLabel);
                    if (predictedLabel == null)
                        continue;
                    else if (predictedLabel.equals(trueLabel)) {
                        correctPredictions++;
                    }
                    totalPredictions++;
                }

                double accuracy = (double) correctPredictions / totalPredictions;
                return accuracy;
            }
        };

        gwo.optimize(fitnessFunction);
        return gwo.getBestSolution();
    }
}
