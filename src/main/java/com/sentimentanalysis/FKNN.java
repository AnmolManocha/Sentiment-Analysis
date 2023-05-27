package com.sentimentanalysis;

import java.util.*;

public class FKNN {
    private int k;
    private double m;
    private List<String[]> trainingSet;

    public FKNN() {
        // Default values for k and m
        this.k = 5;
        this.m = 2.0;
    }

    public FKNN(double[] parameters) {
        this.k = (int) parameters[0];
        this.m = parameters[1];
    }

    public void train(List<String[]> trainingSet) {
        this.trainingSet = trainingSet;
    }

    public String classify(String instance) {
        // Calculate fuzzy membership values for each training instance
        double[] fuzzyMembership = calculateFuzzyMembership(instance);
        System.out.println("Instance: " + instance);
        // System.out.println("Fuzzy Membership Values: " + Arrays.toString(fuzzyMembership));

        // Select k-nearest neighbors based on fuzzy membership values
        List<String[]> nearestNeighbors = selectNearestNeighbors(fuzzyMembership);
        System.out.println("Nearest Neighbors: ");
        for (String[] strings : nearestNeighbors) {
            System.out.print(Arrays.toString(strings) + " ");
        }
        System.out.println();

        // Perform majority voting to determine the sentiment label
        String predictedLabel = performMajorityVoting(nearestNeighbors);
        System.out.println("Predicted Label: " + predictedLabel);

        return predictedLabel;
    }

    private double[] calculateFuzzyMembership(String instance) {
        double[] fuzzyMembership = new double[trainingSet.size()];

        for (int i = 0; i < trainingSet.size(); i++) {
            String[] trainingInstance = trainingSet.get(i);
            double distance = calculateDistance(instance, trainingInstance[0]);

            if (m == 1.0) {
                fuzzyMembership[i] = 1.0; // Assign a default value when m = 1.0
            } else {
                fuzzyMembership[i] = 1.0 / (1.0 + Math.pow(distance, 2.0 / (m - 1.0)));
            }
        }

        return fuzzyMembership;
    }

    private double calculateDistance(String instance1, String instance2) {
        String[] tokens1 = instance1.split(" ");
        String[] tokens2 = instance2.split(" ");

        int minLength = Math.min(tokens1.length, tokens2.length);
        double sum = 0.0;
        int count = 0;

        for (int i = 0; i < minLength; i++) {
            try {
                if (!tokens1[i].isEmpty() && !tokens2[i].isEmpty()) {
                    double value1 = Double.parseDouble(tokens1[i]);
                    double value2 = Double.parseDouble(tokens2[i]);
                    double diff = value1 - value2;
                    sum += Math.pow(diff, 2);
                    count++;
                }
            } catch (NumberFormatException e) {
                // Skip non-numeric tokens
            }
        }

        // Adjust the distance calculation to consider the number of valid numeric
        // tokens
        double distance;
        if (count == 0) {
            distance = Double.MAX_VALUE; // Return a default distance value when no valid tokens are found
        } else {
            distance = Math.sqrt(sum / count);
        }

        return distance;
    }

    private List<String[]> selectNearestNeighbors(final double[] fuzzyMembership) {
        List<String[]> nearestNeighbors = new ArrayList<>();

        // Create a list of indices to keep track of the original position of instances
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < trainingSet.size(); i++) {
            indices.add(i);
        }

        // Sort the indices based on fuzzy membership values in descending order
        Collections.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer index1, Integer index2) {
                return Double.compare(fuzzyMembership[index2], fuzzyMembership[index1]);
            }
        });

        // Select the k-nearest neighbors
        for (int i = 0; i < k; i++) {
            nearestNeighbors.add(trainingSet.get(indices.get(i)));
        }

        return nearestNeighbors;
    }

    private String performMajorityVoting(List<String[]> nearestNeighbors) {
        Map<String, Integer> labelCounts = new HashMap<>();

        // Count the occurrences of each sentiment label among the neighbors
        for (String[] neighbor : nearestNeighbors) {
            String label = neighbor[1];
            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
        }

        // Find the label with the highest count
        int maxCount = 0;
        String predictedLabel = null;
        for (Map.Entry<String, Integer> entry : labelCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                predictedLabel = entry.getKey();
            }
        }

        return predictedLabel;
    }
}
