package com.wcy.wojbackendjudgeservice.controller.inner;

import com.wcy.wojbackendjudgeservice.JudgeService;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.QuestionSubmit;
import com.wcy.wojbackendmodel.model.judge.model.JudgeContextData;
import com.wcy.wojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     *
     * @param
     * @return
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(JudgeContextData judgeContextData) {
        return judgeService.doJudge(judgeContextData);
    }

    @Override
    @PostMapping("/run")
    public QuestionRun runJudge(JudgeContextData judgeContextData) {
        return judgeService.runJudge(judgeContextData);
    }
}
