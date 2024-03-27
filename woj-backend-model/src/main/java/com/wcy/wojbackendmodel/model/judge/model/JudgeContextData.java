package com.wcy.wojbackendmodel.model.judge.model;

import com.wcy.wojbackendmodel.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.wojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import lombok.Data;

/**
 * @author wcy
 * @version 1.0
 * @description: TODO
 * @date 2024/03/26
 */
@Data
public class JudgeContextData {
    private Long id;
    private Long userId;
    private QuestionRunAddRequest questionRunAddRequest;
    private QuestionSubmitAddRequest questionSubmitAddRequest;
}
