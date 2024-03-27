package com.wcy.wojbackendquestionservice.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendcommon.utils.RedisUtil;
import com.wcy.wojbackendmodel.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.User;
import com.wcy.wojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.wcy.wojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.wcy.wojbackendmodel.model.judge.model.JudgeContextData;
import com.wcy.wojbackendmodel.model.vo.QuestionRunVO;
import com.wcy.wojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.wcy.wojbackendquestionservice.service.QuestionRunService;
import com.wcy.wojbackendquestionservice.service.QuestionService;
import com.wcy.wojbackendserviceclient.service.JudgeFeignClient;
import com.wcy.wojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author 王长远
 * @description 针对表【question_run(题目运行表)】的数据库操作Service实现
 * @createDate 2024-01-19 12:38:04
 */
@Service
public class QuestionRunServiceImpl implements QuestionRunService {

    private static final String QUESTION_RUN_PREFIX = "question_run_";


    @Resource
    private RedisUtil redisUtil;
    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;


    @Override
    public Long doQuestionRun(QuestionRunAddRequest questionRunAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionRunAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        // 校验是否有代码
        String code = questionRunAddRequest.getCode();
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码不能为空");
        }
        long questionId = questionRunAddRequest.getQuestionId();
        // 判断题目是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        QuestionRun questionRun = new QuestionRun();
        // 每个用户串行提交题目
        questionRun.setUserId(userId);
        questionRun.setQuestionId(questionId);
        questionRun.setCode(questionRunAddRequest.getCode());
        questionRun.setLanguage(language);
        questionRun.setJudgeCase(JSONUtil.toJsonStr(questionRunAddRequest.getJudgeCase()));
        // 设置初始状态
        questionRun.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionRun.setJudgeInfo("{}");
        long questionRunId = IdUtil.getSnowflakeNextId();
        boolean save = redisUtil.set(QUESTION_RUN_PREFIX + questionRunId, JSONUtil.toJsonStr(questionRun), 60 * 5);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        questionRun.setId(questionRunId);
        // 执行判题服务
//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.runJudge(questionRuntId, questionRunAddRequest);
//        });
        JudgeContextData judgeContextData = new JudgeContextData();
        judgeContextData.setId(questionRunId);
        judgeContextData.setQuestionRunAddRequest(questionRunAddRequest);
        myMessageProducer.sendMessage("code_exchange", "run_routingKey", JSONUtil.toJsonStr(judgeContextData));

        return questionRunId;
    }


    @Override
    public QuestionRunVO getQuestionRunVO(QuestionRun questionRun, User loginUser) {
        QuestionRunVO questionRunVO = QuestionRunVO.objToVo(questionRun);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionRun.getUserId() && !userFeignClient.isAdmin(loginUser)) {
            questionRunVO.setCode(null);
        }
        return questionRunVO;
    }

}




