package com.wcy.wojbackendjudgeservice.judge.codesandbox.impl;


import com.wcy.wojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeRequest;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeResponse;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteStatusEnum;
import com.wcy.wojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.wcy.wojbackendmodel.model.judge.model.JudgeInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(ExecuteStatusEnum.SUCCESS.getCode());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
