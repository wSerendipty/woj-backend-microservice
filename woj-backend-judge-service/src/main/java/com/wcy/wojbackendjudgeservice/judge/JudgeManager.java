package com.wcy.wojbackendjudgeservice.judge;

import com.wcy.wojbackendjudgeservice.judge.codesandbox.strategy.DefaultJudgeStrategy;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.strategy.JavaLanguageJudgeStrategy;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.strategy.JudgeContext;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.strategy.JudgeStrategy;
import com.wcy.wojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.wcy.wojbackendmodel.model.judge.model.JudgeInfo;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        String language = judgeContext.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (QuestionSubmitLanguageEnum.getEnumByValue(language) == QuestionSubmitLanguageEnum.JAVA) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
