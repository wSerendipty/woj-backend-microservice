package com.wcy.wojbackendquestionservice.controller.inner;

import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.QuestionStatus;
import com.wcy.wojbackendmodel.model.entity.QuestionSubmit;
import com.wcy.wojbackendquestionservice.service.QuestionRunService;
import com.wcy.wojbackendquestionservice.service.QuestionService;
import com.wcy.wojbackendquestionservice.service.QuestionStatusService;
import com.wcy.wojbackendquestionservice.service.QuestionSubmitService;
import com.wcy.wojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionStatusService questionStatusService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @Override
    public Boolean updateQuestionById(Question question) {
        return questionService.updateById(question);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

    @Override
    public QuestionStatus getByQuestionIdAndUserIdAndType(Long questionId, long userId, String type) {
        return questionStatusService.getByQuestionIdAndUserIdAndType(questionId,userId,type);
    }

    @Override
    public Boolean updateQuestionStatusById(QuestionStatus questionStatus) {
        return questionStatusService.updateById(questionStatus);
    }


}
