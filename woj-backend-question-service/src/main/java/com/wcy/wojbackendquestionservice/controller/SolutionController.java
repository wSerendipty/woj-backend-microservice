package com.wcy.wojbackendquestionservice.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcy.wojbackendcommon.annotation.AuthCheck;
import com.wcy.wojbackendcommon.common.BaseResponse;
import com.wcy.wojbackendcommon.common.DeleteRequest;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.common.ResultUtils;
import com.wcy.wojbackendcommon.constant.UserConstant;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendcommon.exception.ThrowUtils;
import com.wcy.wojbackendmodel.model.dto.solution.SolutionAddRequest;
import com.wcy.wojbackendmodel.model.dto.solution.SolutionEditRequest;
import com.wcy.wojbackendmodel.model.dto.solution.SolutionQueryRequest;
import com.wcy.wojbackendmodel.model.entity.Question;
import com.wcy.wojbackendmodel.model.entity.Solution;
import com.wcy.wojbackendmodel.model.entity.User;
import com.wcy.wojbackendmodel.model.vo.SolutionVO;
import com.wcy.wojbackendquestionservice.service.QuestionService;
import com.wcy.wojbackendquestionservice.service.SolutionService;
import com.wcy.wojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/solution")
@Slf4j
public class SolutionController {

    @Resource
    private SolutionService solutionService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;


    /**
     * 创建
     *
     * @param solutionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSolution(@RequestBody SolutionAddRequest solutionAddRequest, HttpServletRequest request) {
        if (solutionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = new Solution();
        BeanUtils.copyProperties(solutionAddRequest, solution);
        List<String> tags = solutionAddRequest.getTags();
        if (tags != null) {
            solution.setTags(JSONUtil.toJsonStr(tags));
        }
        List<String> specialTags = solutionAddRequest.getSpecialTags();
        if (specialTags != null) {
            solution.setSpecialTags(JSONUtil.toJsonStr(specialTags));
        }
        solutionService.validPost(solution, true);
        User loginUser = userFeignClient.getLoginUser(request);
        solution.setUserId(loginUser.getId());
        boolean result = solutionService.save(solution);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        Long questionId = solution.getQuestionId();
        // 更新题目题解数
        Question question = questionService.getById(questionId);
        question.setSolutionNum(question.getSolutionNum() + 1);
        boolean b = questionService.updateById(question);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        long newSolutionId = solution.getId();
        return ResultUtils.success(newSolutionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSolution(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Solution oldSolution = solutionService.getById(id);
        ThrowUtils.throwIf(oldSolution == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldSolution.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = solutionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param solutionEditRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSolution(@RequestBody SolutionEditRequest solutionEditRequest) {
        if (solutionEditRequest == null || solutionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = new Solution();
        BeanUtils.copyProperties(solutionEditRequest, solution);
        List<String> tags = solutionEditRequest.getTags();
        if (tags != null) {
            solution.setTags(JSONUtil.toJsonStr(tags));
        }
        List<String> specialTags = solutionEditRequest.getSpecialTags();
        if (specialTags != null) {
            solution.setSpecialTags(JSONUtil.toJsonStr(specialTags));
        }
        if (specialTags == null) {
            solution.setSpecialTags(null);
        }
        // 参数校验
        solutionService.validPost(solution, false);
        long id = solutionEditRequest.getId();
        // 判断是否存在
        Solution oldSolution = solutionService.getById(id);
        ThrowUtils.throwIf(oldSolution == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = solutionService.updateById(solution);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<SolutionVO> getSolutionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = solutionService.getById(id);
        if (solution == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(solutionService.getSolutionVO(solution, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param solutionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SolutionVO>> listSolutionVOByPage(@RequestBody SolutionQueryRequest solutionQueryRequest,
                                                       HttpServletRequest request) {
        long current = solutionQueryRequest.getCurrent();
        long size = solutionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Solution> solutionPage = solutionService.page(new Page<>(current, size),
                solutionService.getQueryWrapper(solutionQueryRequest));
        return ResultUtils.success(solutionService.getSolutionVOPage(solutionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param solutionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<SolutionVO>> listMySolutionVOByPage(@RequestBody SolutionQueryRequest solutionQueryRequest,
                                                         HttpServletRequest request) {
        if (solutionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        solutionQueryRequest.setUserId(loginUser.getId());
        long current = solutionQueryRequest.getCurrent();
        long size = solutionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Solution> solutionPage = solutionService.page(new Page<>(current, size),
                solutionService.getQueryWrapper(solutionQueryRequest));
        return ResultUtils.success(solutionService.getSolutionVOPage(solutionPage, request));
    }


    /**
     * 编辑（用户）
     *
     * @param solutionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSolution(@RequestBody SolutionEditRequest solutionEditRequest, HttpServletRequest request) {
        if (solutionEditRequest == null || solutionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = new Solution();
        BeanUtils.copyProperties(solutionEditRequest, solution);
        List<String> tags = solutionEditRequest.getTags();
        if (tags != null) {
            solution.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        solutionService.validPost(solution, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = solutionEditRequest.getId();
        // 判断是否存在
        Solution oldSolution = solutionService.getById(id);
        ThrowUtils.throwIf(oldSolution == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldSolution.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = solutionService.updateById(solution);
        return ResultUtils.success(result);
    }


}
