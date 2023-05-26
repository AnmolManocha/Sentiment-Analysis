package com.sentimentanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

/**
 * DataPreprocessing
 * 
 * @author Anmol Manocha
 */

public class DataPreprocessing {
    private static final String DATASET_PATH = "src/main/java/com/sentimentanalysis/data/testdata.manual.2009.06.14.csv";

    // Read the Sentiment140 CSV file and extract the tweet text and sentiment
    // labels
    public static List<String[]> readCSVFile(String csvFile) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String tweet = parts[5]; // Assuming the tweet text is in the 6th column
                String sentimentLabel = parts[0]; // Assuming the sentiment label is in the 1st column
                String[] entry = { tweet, sentimentLabel };
                data.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Preprocess the tweets by removing noise, punctuation, and special characters
    public static List<String[]> preprocessTweets(List<String[]> data) {
        List<String[]> preprocessedData = new ArrayList<>();

        // Set up Stanford CoreNLP pipeline for tokenization and stemming
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        for (String[] entry : data) {
            String tweet = entry[0];
            String sentimentLabel = entry[1];

            // Remove URLs, hashtags, and mentions
            String preprocessedTweet = tweet.replaceAll("(http|https)://\\S+|www\\.\\S+|#\\S+|@\\S+", "")
                    .replaceAll("\\W+", " ").toLowerCase().trim();

            // Tokenize and stem the preprocessed tweet
            Annotation document = new Annotation(preprocessedTweet);
            pipeline.annotate(document);

            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            StringBuilder tokenizedTweet = new StringBuilder();
            for (CoreMap sentence : sentences) {
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                    tokenizedTweet.append(lemma).append(" ");
                }
            }

            String[] preprocessedEntry = { tokenizedTweet.toString().trim(), sentimentLabel };
            preprocessedData.add(preprocessedEntry);
        }
        return preprocessedData;
    }

    public List<String[]> preprocessData() {
        List<String[]> data = readCSVFile(DATASET_PATH);
        List<String[]> preprocessedData = preprocessTweets(data);
        return preprocessedData;
    }
}