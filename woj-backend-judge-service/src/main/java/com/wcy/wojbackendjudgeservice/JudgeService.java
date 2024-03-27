package com.wcy.wojbackendjudgeservice;


import com.wcy.wojbackendmodel.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.QuestionSubmit;
import com.wcy.wojbackendmodel.model.judge.model.JudgeContextData;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     * @param
     * @return
     */
    QuestionSubmit doJudge(JudgeContextData judgeContextData);


    QuestionRun runJudge(JudgeContextData judgeContextData);



}
