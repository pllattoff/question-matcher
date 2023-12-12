package org.example.repository;

import org.example.model.SimilarQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SimilarQuestionRepository extends JpaRepository<SimilarQuestion, Integer> {

    @Query(value = "select id, question " +
                   "from question_matcher.questions " +
                   "where question like (:first_word ||' %') ", nativeQuery = true)
    List<SimilarQuestion> findQuestionsByFirstWord(@Param("first_word") String firstWord);

}