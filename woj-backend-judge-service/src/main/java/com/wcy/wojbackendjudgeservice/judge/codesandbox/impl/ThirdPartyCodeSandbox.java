package com.wcy.wojbackendjudgeservice.judge.codesandbox.impl;

import com.wcy.wojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeRequest;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
