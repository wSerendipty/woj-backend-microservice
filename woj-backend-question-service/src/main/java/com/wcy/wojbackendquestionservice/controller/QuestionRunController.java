package com.wcy.wojbackendquestionservice.controller;

import cn.hutool.json.JSONUtil;
import com.wcy.wojbackendcommon.common.BaseResponse;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.common.ResultUtils;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendcommon.utils.RedisUtil;
import com.wcy.wojbackendmodel.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.User;
import com.wcy.wojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.wcy.wojbackendmodel.model.vo.QuestionRunVO;
import com.wcy.wojbackendquestionservice.service.QuestionRunService;
import com.wcy.wojbackendserviceclient.service.UserFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/17 10:09
 */
@RestController
@RequestMapping("/question_run")
public class QuestionRunController {

    private static final String QUESTION_RUN_PREFIX = "question_run_";

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionRunService questionRunService;

    /**
     * 运行题目
     *
     * @return 运行记录的 id
     */
    @PostMapping("/run")
    public BaseResponse<Long> doQuestionRun(@RequestBody QuestionRunAddRequest questionRunAddRequest,
                                            HttpServletRequest request) {
        if (questionRunAddRequest == null || questionRunAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userFeignClient.getLoginUser(request);
        Long questionRunId = questionRunService.doQuestionRun(questionRunAddRequest, loginUser);
        return ResultUtils.success(questionRunId);
    }


    /**
     * 获取题目运行ByID（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     */
    @GetMapping("/get")
    public BaseResponse<QuestionRunVO> getQuestionRunById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object questionRunStr = redisUtil.get(QUESTION_RUN_PREFIX + id);
        if (questionRunStr == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据不存在");
        }

        QuestionRun questionRun = JSONUtil.toBean(questionRunStr.toString(), QuestionRun.class);
        // 如果运行状态成功或者失败，则删除缓存
        if (Objects.equals(questionRun.getStatus(), QuestionSubmitStatusEnum.SUCCEED.getValue()) ||
                Objects.equals(questionRun.getStatus(), QuestionSubmitStatusEnum.FAILED.getValue())) {
            redisUtil.del(QUESTION_RUN_PREFIX + id);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionRunService.getQuestionRunVO(questionRun, loginUser));
    }



}
