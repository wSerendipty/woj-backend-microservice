package com.wcy.wojbackendjudgeservice.service.impl;

import cn.hutool.json.JSONUtil;

import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendcommon.utils.RedisUtil;
import com.wcy.wojbackendjudgeservice.JudgeService;
import com.wcy.wojbackendjudgeservice.judge.JudgeManager;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.wcy.wojbackendjudgeservice.judge.codesandbox.strategy.JudgeContext;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeRequest;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteCodeResponse;
import com.wcy.wojbackendmodel.model.codesandbox.model.ExecuteStatusEnum;
import com.wcy.wojbackendmodel.model.dto.question.JudgeCase;
import com.wcy.wojbackendmodel.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.wojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendmodel.model.entity.QuestionRun;
import com.wcy.wojbackendmodel.model.entity.QuestionStatus;
import com.wcy.wojbackendmodel.model.entity.QuestionSubmit;
import com.wcy.wojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.wcy.wojbackendmodel.model.enums.QuestionStatusEnum;
import com.wcy.wojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.wcy.wojbackendmodel.model.judge.model.JudgeContextData;
import com.wcy.wojbackendmodel.model.judge.model.JudgeInfo;
import com.wcy.wojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private RedisUtil redisUtil;

    private static final String QUESTION_RUN_PREFIX = "question_run_";

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    private static final String MAIN = "Main.java";


    /**
     * @description: 提交判题
     * @author wcy
     * @date: 2024/3/22
     */
    @Override
    public QuestionSubmit doJudge(JudgeContextData judgeContextData) {
        Long questionSubmitId = judgeContextData.getId();
        Long userId = judgeContextData.getUserId();
        QuestionSubmitAddRequest questionSubmitAddRequest = judgeContextData.getQuestionSubmitAddRequest();


        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        if (executeCodeResponse == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行沙箱");
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        Integer status = executeCodeResponse.getStatus();
        JudgeContext judgeContext = new JudgeContext();
        JudgeInfo executeJudgeInfo = executeCodeResponse.getJudgeInfo();
        if (executeJudgeInfo == null) {
            executeJudgeInfo = new JudgeInfo();
        }
        if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.COMPILE_ERROR)) {
            // 编译错误
            String message = executeCodeResponse.getMessage();
            int indexOf = message.indexOf(MAIN);
            executeJudgeInfo.setMessage(message.substring(indexOf));
            executeJudgeInfo.setStatus(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            //修改数据库中的判题结果
            questionSubmitUpdate = updateQuestionSubmitStatus(executeJudgeInfo, questionSubmitId);
            return questionSubmitUpdate;
        } else if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.RUNTIME_ERROR)) {
            // 运行时错误
            String message = executeCodeResponse.getMessage();
            int indexOf = message.indexOf(MAIN);
            executeJudgeInfo.setMessage(message.substring(indexOf));
            executeJudgeInfo.setStatus(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
            // 修改数据库中的判题结果
            questionSubmitUpdate = updateQuestionSubmitStatus(executeJudgeInfo, questionSubmitId);
            return questionSubmitUpdate;
        }
        judgeContext.setJudgeInfo(executeJudgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setLanguage(language);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        if (judgeInfo.getStatus().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())) {
            // 5.1）如果判题结果为通过，就更新题目的通过数 + 1
            Question questionUpdate = new Question();
            questionUpdate.setId(questionId);
            questionUpdate.setAcceptedNum(question.getAcceptedNum() + 1);
            boolean updateQuestion = questionFeignClient.updateQuestionById(questionUpdate);
            if (!updateQuestion) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            // 5.2）就修改题目的状态为已通过
            // 1. 查询题目状态
            QuestionStatus questionStatus = questionFeignClient.getByQuestionIdAndUserIdAndType(questionId, userId, questionSubmitAddRequest.getType());
            questionStatus.setId(questionStatus.getId());
            questionStatus.setStatus(QuestionStatusEnum.ACCEPTED.getValue());
            boolean updateQuestionStatus = questionFeignClient.updateQuestionStatusById(questionStatus);
            if (!updateQuestionStatus) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
        }

        // 6）修改数据库中的判题结果
        questionSubmitUpdate = updateQuestionSubmitStatus(judgeInfo, questionSubmitId);
        return questionSubmitUpdate;

    }

    /**
     * @description: 运行判题（用于测试题目提交的代码）, 运行判题的代码，获取到执行结果，设置题目的判题状态和信息
     * @author wcy
     * @date: 2024/3/22
     */
    @Override
    public QuestionRun runJudge(JudgeContextData judgeContextData) {
        Long questionRunId = judgeContextData.getId();
        QuestionRunAddRequest questionRunAddRequest = judgeContextData.getQuestionRunAddRequest();
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        Object questionRunStr = redisUtil.get(QUESTION_RUN_PREFIX + questionRunId);
        if (questionRunStr == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "运行信息不存在");
        }
        QuestionRun questionRun = JSONUtil.toBean(questionRunStr.toString(), QuestionRun.class);
        Long questionId = questionRun.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目运行状态不为等待中，就不用重复执行了
        if (!questionRun.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在运行中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        questionRun.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = redisUtil.set(QUESTION_RUN_PREFIX + questionRunId, JSONUtil.toJsonStr(questionRun));
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionRun.getLanguage();
        String code = questionRun.getCode();
        // 获取输入用例
        String judgeCaseStr = questionRun.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String> expectOutList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        if (executeCodeResponse == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行沙箱");
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        Integer status = executeCodeResponse.getStatus();
        JudgeContext judgeContext = new JudgeContext();
        JudgeInfo executeJudgeInfo = executeCodeResponse.getJudgeInfo();
        if (executeJudgeInfo == null) {
            executeJudgeInfo = new JudgeInfo();
        }
        if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.COMPILE_ERROR)) {
            // 编译错误
            String message = executeCodeResponse.getMessage();
            int indexOf = message.indexOf(MAIN);
            executeJudgeInfo.setMessage(message.substring(indexOf));
            executeJudgeInfo.setStatus(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            //修改redis中的判题结果
            questionRun = updateQuestionRunStatus(questionRun, executeJudgeInfo, questionRunId);
            return questionRun;
        } else if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.RUNTIME_ERROR)) {
            String message = executeCodeResponse.getMessage();
            int indexOf = message.indexOf(MAIN);
            executeJudgeInfo.setMessage(message.substring(indexOf));
            executeJudgeInfo.setStatus(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
            // 修改redis中的判题结果
            questionRun = updateQuestionRunStatus(questionRun, executeJudgeInfo, questionRunId);
            return questionRun;
        }
        judgeContext.setJudgeInfo(executeJudgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setLanguage(language);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        judgeInfo.setInput(inputList);
        judgeInfo.setOutput(outputList);
        // todo 修改运行JudgeInfo的代码
        judgeInfo.setExpectedOutput(expectOutList);
        // 6）修改redis中的判题结果
        questionRun = updateQuestionRunStatus(questionRun, judgeInfo, questionRunId);
        return questionRun;
    }


    public QuestionSubmit updateQuestionSubmitStatus(JudgeInfo judgeInfo, long questionSubmitId) {
        // 6）修改数据库中的判题结果
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }


    public QuestionRun updateQuestionRunStatus(QuestionRun questionRun, JudgeInfo judgeInfo, long questionRunId) {
        // 6）修改数据库中的判题结果
        questionRun.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionRun.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        boolean update = redisUtil.set(QUESTION_RUN_PREFIX + questionRunId, JSONUtil.toJsonStr(questionRun));
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionRun;
    }

}
