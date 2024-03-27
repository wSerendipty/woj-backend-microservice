package com.wcy.wojbackendjudgeservice.judge.codesandbox;


import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeRequest;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
