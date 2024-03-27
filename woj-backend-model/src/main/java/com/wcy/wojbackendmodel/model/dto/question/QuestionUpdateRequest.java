package com.wcy.wojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
*
 */
@Data
public class QuestionUpdateRequest implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题目难度（简单、中等、困难）
     */
    private String difficulty;

    /**
     * 题目状态（0：未开始，1：通过 2：尝试过）
     */
    private Integer status;

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 测试用例
     */
    private List<JudgeCase> testJudgeCase;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}