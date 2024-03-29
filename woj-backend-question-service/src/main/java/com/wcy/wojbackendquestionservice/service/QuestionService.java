package com.wcy.wojbackendquestionservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.wojbackendmodel.model.dto.daily.DailyQueryRequest;
import com.wcy.wojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendmodel.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 王长远
* @description 针对表【question(题目)】的数据库操作Service
* 
*/
public interface QuestionService extends IService<Question> {


    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, String type, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);


    /**
     * 获取题目封装（管理员）
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVOAdmin(Question question, HttpServletRequest request);

    /**
     * 获取每日一题封装
     * @param dailyQueryRequest
     * @param request
     * @return
     */
    QuestionVO getDailyQuestionVO(DailyQueryRequest dailyQueryRequest, HttpServletRequest request);


}
