package com.wcy.wojbackendjudgeservice.judge.codesandbox.strategy;


import com.wcy.wojbackendmodel.model.dto.question.JudgeCase;
import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendmodel.model.judge.model.JudgeInfo;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private String language;

}
