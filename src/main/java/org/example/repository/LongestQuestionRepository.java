package org.example.repository;

import org.example.model.LongestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LongestQuestionRepository extends JpaRepository<LongestQuestion, Integer> {

    @Query(value = "select id, question, length(question) " +
                   "from question_matcher.questions " +
                   "order by length(question) desc " +
                   "limit :limit", nativeQuery = true)
    List<LongestQuestion> findLongestQuestions(@Param("limit") Integer limit);

}