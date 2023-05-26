package com.sentimentanalysis;

import java.util.List;

/**
 * Main
 * 
 * @author Anmol Manocha
 */
public class Main {
    public static void main(String[] args) {
        // * Create an instance of DataPreprocessing and preprocess the data
        DataPreprocessing dataPreprocessing = new DataPreprocessing();
        List<String[]> preprocessedData = dataPreprocessing.preprocessData();

        // * Print Preprocessed Data
        // for (String[] tweet : preprocessedData) {
        // System.out.println(tweet[1] + ", " + tweet[0]);
        // }

        // * Split the preprocessed data into training and testing sets
        DatasetSplitter datasetSplitter = new DatasetSplitter(preprocessedData, 0.8);
        List<List<String[]>> splittedDataset = datasetSplitter.splitDataset();
        List<String[]> trainingSet = splittedDataset.get(0);
        List<String[]> testingSet = splittedDataset.get(1);

        // * Create an instance of SentimentAnalysis and perform sentiment analysis
        SentimentAnalysis sentimentAnalysis = new SentimentAnalysis(trainingSet,
                testingSet);
    }
}
