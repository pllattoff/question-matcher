package org.example.controller;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import org.example.entity.LongestQuestionsRequest;
import org.example.entity.SimilarQuestionsRequest;
import org.example.service.QuestionMatcherService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionMatcherController {

    Logger log = (Logger) LoggerFactory.getLogger(this.getClass());

    private final QuestionMatcherService questionMatcherService;

    @PostMapping(value = "/getLongestQuestions")
    public List<String> getLongestQuestions(@RequestBody LongestQuestionsRequest longestQuestionsRequest) {
        log.info("/getLongestQuestions : " + longestQuestionsRequest);

        Integer top = longestQuestionsRequest.getTop();

        if (top == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty value in one of the required params: top");
        }

        List<String> questions = questionMatcherService.getLongestQuestions(top);

        return questions;
    }

    @PostMapping(value = "/getSimilarQuestions")
    public List<String> getSimilarQuestions(@RequestBody SimilarQuestionsRequest similarQuestionsRequest) {
        log.info("/getSimilarQuestions : " + similarQuestionsRequest);

        String question = similarQuestionsRequest.getQuestion();
        Integer quantityOfSimilar = similarQuestionsRequest.getQuantityOfSimilar();

        if (question == null || quantityOfSimilar == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty value in one of the required params: question, quantityOfSimilar");
        }

        List<String> questions = questionMatcherService.getSimilarQuestions(question, quantityOfSimilar);

        return questions;
    }

}
