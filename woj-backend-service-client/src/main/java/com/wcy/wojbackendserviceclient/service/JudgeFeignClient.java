package com.wcy.wojbackendserviceclient.service;


import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.QuestionSubmit;
import com.wcy.wojbackendmodel.model.judge.model.JudgeContextData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 判题服务
 */
@FeignClient(name = "woj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {

    /**
     * 判题
     * @param
     * @return
     */
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestBody JudgeContextData judgeContextData);


    @PostMapping("/run")
    QuestionRun runJudge(@RequestBody JudgeContextData judgeContextData);


}
