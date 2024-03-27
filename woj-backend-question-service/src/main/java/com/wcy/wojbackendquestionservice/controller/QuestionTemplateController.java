package com.wcy.wojbackendquestionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcy.wojbackendcommon.annotation.AuthCheck;
import com.wcy.wojbackendcommon.common.BaseResponse;
import com.wcy.wojbackendcommon.common.DeleteRequest;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.common.ResultUtils;
import com.wcy.wojbackendcommon.constant.UserConstant;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendcommon.exception.ThrowUtils;
import com.wcy.wojbackendmodel.model.dto.questionTemplate.QuestionTemplateQueryRequest;
import com.wcy.wojbackendmodel.model.entity.QuestionTemplate;
import com.wcy.wojbackendquestionservice.service.QuestionTemplateService;
import com.wcy.wojbackendserviceclient.service.UserFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/8 9:57
 */
@RestController
@RequestMapping("/template")
public class QuestionTemplateController {
    @Resource
    private QuestionTemplateService questionTemplateService;
    @Resource
    private UserFeignClient userFeignClient;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionTemplate(@RequestBody QuestionTemplate questionTemplate, HttpServletRequest request) {
        if (questionTemplate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        questionTemplate.setUserId(userFeignClient.getLoginUser(request).getId());
        questionTemplateService.validQuestion(questionTemplate, true);
        boolean b = questionTemplateService.save(questionTemplate);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(questionTemplate.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> deleteQuestionTemplate(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = questionTemplateService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("删除成功");
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> updateQuestionTemplate(@RequestBody QuestionTemplate questionTemplate) {
        if (questionTemplate == null || questionTemplate.getId() == null || questionTemplate.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        questionTemplateService.validQuestion(questionTemplate, false);
        boolean b = questionTemplateService.updateById(questionTemplate);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("更新成功");
    }

    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionTemplate>> listQuestionTemplate(@RequestBody QuestionTemplateQueryRequest questionTemplateQueryRequest) {
        if (questionTemplateQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionTemplateQueryRequest.getCurrent();
        long size = questionTemplateQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionTemplate> page = questionTemplateService.page(new Page<>(current, size),
                questionTemplateService.getQueryWrapper(questionTemplateQueryRequest));
        return ResultUtils.success(page);
    }
}
