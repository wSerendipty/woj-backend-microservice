package com.wcy.wojbackendquestionservice.service;


import com.wcy.wojbackendmodel.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.User;
import com.wcy.wojbackendmodel.model.vo.QuestionRunVO;

/**
* @author 王长远
*/
public interface QuestionRunService {
    /**
     * 题目运行
     *
     * @param questionRunAddRequest 题目运行信息
     * @param loginUser
     * @return
     */
    Long doQuestionRun(QuestionRunAddRequest questionRunAddRequest, User loginUser);



    /**
     * 获取题目封装
     *
     * @param questionRun
     * @param loginUser
     * @return
     */
    QuestionRunVO getQuestionRunVO(QuestionRun questionRun, User loginUser);

}
