package com.sentimentanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DatasetSplitter
 * 
 * @author Anmol Manocha
 */

public class DatasetSplitter {
    private List<String[]> dataset;
    private double trainRatio;

    public DatasetSplitter(List<String[]> dataset, double trainRatio) {
        this.dataset = dataset;
        this.trainRatio = trainRatio;
    }

    // Split the dataset into training and testing sets
    public List<List<String[]>> splitDataset() {
        List<List<String[]>> splittedDataset = new ArrayList<>();

        // Shuffle the dataset to randomize the order
        Collections.shuffle(dataset);

        // Determine the split index based on the train ratio
        int splitIndex = (int) (dataset.size() * trainRatio);

        // Create the training set by selecting the portion before the split index
        List<String[]> trainingSet = dataset.subList(0, splitIndex);

        // Create the testing set by selecting the portion after the split index
        List<String[]> testingSet = dataset.subList(splitIndex, dataset.size());

        splittedDataset.add(trainingSet);
        splittedDataset.add(testingSet);

        return splittedDataset;
    }
}