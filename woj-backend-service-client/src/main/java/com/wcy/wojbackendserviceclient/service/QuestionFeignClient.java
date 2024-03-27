package com.wcy.wojbackendserviceclient.service;


import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.QuestionStatus;
import com.wcy.wojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author 王长远
* @description 针对表【question(题目)】的数据库操作Service
* 
*/
@FeignClient(name = "woj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    @PostMapping("/question/update")
    Boolean updateQuestionById(@RequestBody Question question);

    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);

    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

    @GetMapping("/question_status/get")
    QuestionStatus getByQuestionIdAndUserIdAndType(@RequestParam("questionId") Long questionId, @RequestParam("userId") long userId, @RequestParam("type") String type);

    @PostMapping("/question_status/update")
    Boolean updateQuestionStatusById(@RequestBody QuestionStatus questionStatus);
}
