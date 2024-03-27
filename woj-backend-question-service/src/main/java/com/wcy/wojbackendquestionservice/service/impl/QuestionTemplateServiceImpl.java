package com.wcy.wojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.constant.CommonConstant;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendcommon.exception.ThrowUtils;
import com.wcy.wojbackendcommon.utils.SqlUtils;
import com.wcy.wojbackendmodel.model.dto.questionTemplate.QuestionTemplateQueryRequest;
import com.wcy.wojbackendmodel.model.entity.QuestionTemplate;
import com.wcy.wojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.wcy.wojbackendquestionservice.mapper.QuestionTemplateMapper;
import com.wcy.wojbackendquestionservice.service.QuestionTemplateService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王长远
 * @description 针对表【question_template(题目提交)】的数据库操作Service实现
 * @createDate 2024-01-22 09:18:36
 */
@Service
public class QuestionTemplateServiceImpl extends ServiceImpl<QuestionTemplateMapper, QuestionTemplate>
        implements QuestionTemplateService {

    @Override
    public void validQuestion(QuestionTemplate questionTemplate, boolean add) {
        if (questionTemplate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String language = questionTemplate.getLanguage();
        String code = questionTemplate.getCode();
        Long questionId = questionTemplate.getQuestionId();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.isEmpty(questionId), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isAnyBlank( code, language), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (QuestionSubmitLanguageEnum.getEnumByValue(language) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "语言不合法");
        }
        if (StringUtils.isNotBlank(code) && code.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public QueryWrapper<QuestionTemplate> getQueryWrapper(QuestionTemplateQueryRequest questionTemplateQueryRequest) {
        QueryWrapper<QuestionTemplate> queryWrapper = new QueryWrapper<>();
        if (questionTemplateQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionTemplateQueryRequest.getId();
        Long questionId = questionTemplateQueryRequest.getQuestionId();
        String language = questionTemplateQueryRequest.getLanguage();
        Long userId = questionTemplateQueryRequest.getUserId();
        String sortField = questionTemplateQueryRequest.getSortField();
        String sortOrder = questionTemplateQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public List<QuestionTemplate> listByQuestionId(Long questionId) {
        QueryWrapper<QuestionTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("questionId", questionId);
        List<QuestionTemplate> questionTemplates = this.list(queryWrapper);
        return questionTemplates;
    }
}




