package com.wcy.wojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.wojbackendmodel.model.dto.questionStatus.QuestionStatusQueryRequest;
import com.wcy.wojbackendmodel.model.entity.QuestionStatus;
import com.wcy.wojbackendmodel.model.vo.QuestionFinishVO;


import javax.servlet.http.HttpServletRequest;

/**
* @author 王长远
* @description 针对表【question_status(题目状态表)】的数据库操作Service
* @createDate 2024-01-27 17:37:17
*/
public interface QuestionStatusService extends IService<QuestionStatus> {

    QuestionStatus getByQuestionIdAndUserIdAndType(Long questionId,long userId, String type);


    /**
     * 获取查询条件
     *
     * @param questionStatusQueryRequest
     * @return
     */
    QueryWrapper<QuestionStatus> getQueryWrapper(QuestionStatusQueryRequest questionStatusQueryRequest);

    /**
     * 获取题目完成状态封装
     *
     * @param questionStatusQueryRequest
     * @param request
     * @return
     */
    QuestionFinishVO getQuestionFinish(QuestionStatusQueryRequest questionStatusQueryRequest, HttpServletRequest request);




}
