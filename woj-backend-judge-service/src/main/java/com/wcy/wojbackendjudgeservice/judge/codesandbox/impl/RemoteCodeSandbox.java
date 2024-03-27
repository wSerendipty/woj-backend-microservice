package com.wcy.wojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeRequest;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
@Component
public class RemoteCodeSandbox implements CodeSandbox {


    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "Authorization";

    private static final String WAY = "original" ;// original: 原生实现，docker 为 docker 实现


    private static final String AUTH_REQUEST_SECRET = MD5.create().digestHex("Wcy0626..");


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = ""; // docker沙箱
        if (WAY.equals("docker")){
//            url = "http://172.17.213.195:8181/executeCode/docker";
            url = "https://codesandbox.serendipty.xyz/executeCode/docker";
//            url = "http://localhost:8181/executeCode/docker";
        }else {
//            url = "https://codesandbox.serendipty.xyz/executeCode";
            url = "http://localhost:8181/executeCode";
        }
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }else if (responseStr.contains("Error request")){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
