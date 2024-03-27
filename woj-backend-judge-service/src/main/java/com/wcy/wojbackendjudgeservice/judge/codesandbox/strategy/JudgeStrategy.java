package com.wcy.wojbackendjudgeservice.judge.codesandbox.strategy;


import com.wcy.wojbackendmodel.model.judge.model.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
