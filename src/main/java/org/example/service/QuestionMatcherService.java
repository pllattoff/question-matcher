package org.example.service;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import org.example.model.LongestQuestion;
import org.example.model.Question;
import org.example.model.SimilarQuestion;
import org.example.repository.LongestQuestionRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.SimilarQuestionRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionMatcherService {

    Logger log = (Logger) LoggerFactory.getLogger(this.getClass());

    private final LongestQuestionRepository longestQuestionRepository;
    private final SimilarQuestionRepository similarQuestionRepository;
    private final QuestionRepository questionRepository;

    public List<String> getLongestQuestions(Integer top) {

        List<String> questions = longestQuestionRepository.findLongestQuestions(top).stream()
                .map(LongestQuestion::getQuestion)
                .collect(Collectors.toList());

        return questions;
    }

    public List<String> getSimilarQuestions(String question, int quantityOfSimilar) {

        String[] words = splitBySpaces(question);
        String firstWord = words[0];

        List<String> questionsByFirstWord = similarQuestionRepository.findQuestionsByFirstWord(firstWord).stream()
                .map(SimilarQuestion::getQuestion)
                .collect(Collectors.toList());

        List<String> questions = this.getMostSimilarQuestions(question, questionsByFirstWord, quantityOfSimilar);

        if (questions.isEmpty()) {
            this.saveQuestion(question);
        }

        return questions;
    }

    public List<String> getMostSimilarQuestions(String question, List<String> candidateQuestions, int quantityOfSimilar) {

        Map<String, Double> similarityMap = this.countSimilarity(question, candidateQuestions);

        List<String> sortedQuestions = this.sortBySimilarity(similarityMap);

        sortedQuestions = this.getRequiredQuantity(sortedQuestions, quantityOfSimilar);

        return sortedQuestions;
    }

    private Map<String, Double> countSimilarity(String question, List<String> candidateQuestions) {

        Map<String, Double> similarityMap = new ConcurrentHashMap<>();

        String[] questionWords = splitBySpaces(question);

        candidateQuestions.parallelStream().forEach(candidate -> {
            String[] candidateWords = splitBySpaces(candidate);

            int countCandidateWords = 0;
            int countSimilarWords = 0;

            for (String candidateWord : candidateWords) {
                if (candidateWord.length() > 3) {
                    countCandidateWords++;
                    if (containsIgnoreCase(questionWords, candidateWord)) {
                        countSimilarWords++;
                    }
                }
            }

            double similarity = (double) countSimilarWords / countCandidateWords;
            similarityMap.put(candidate, similarity);
        });

        return similarityMap;
    }

    private List<String> sortBySimilarity(Map<String, Double> similarityMap) {

        List<Map.Entry<String, Double>> sortedSimilarityList = similarityMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        for (Map.Entry<String, Double> entry : sortedSimilarityList) {
            log.info("Питання: " + entry.getKey() + "; Схожість: " + entry.getValue());
        }

        List<String> sortedQuestions = sortedSimilarityList.stream().map(Map.Entry::getKey).collect(Collectors.toList());

        return sortedQuestions;
    }

    private List<String> getRequiredQuantity(List<String> questions, int quantityOfSimilar) {

        if (questions.size() > quantityOfSimilar) {
            questions = questions.subList(0, quantityOfSimilar);
        }

        return questions;
    }

    private static boolean containsIgnoreCase(String[] words, String targetWord) {
        for (String word : words) {
            if (word.equalsIgnoreCase(targetWord)) {
                return true;
            }
        }
        return false;
    }

    private static String[] splitBySpaces(String line) {
        return line.split("\\s+");
    }

    public void saveQuestion(String questionText) {
        Question question = new Question();
        question.setQuestion(questionText);
        questionRepository.save(question);
    }

}
