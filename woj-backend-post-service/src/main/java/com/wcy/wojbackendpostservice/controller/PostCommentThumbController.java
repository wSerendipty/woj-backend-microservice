package com.wcy.wojbackendpostservice.controller;

import com.wcy.wojbackendcommon.common.BaseResponse;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.common.ResultUtils;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendmodel.model.dto.postCommentThumb.PostCommentThumbAddRequest;
import com.wcy.wojbackendmodel.model.entity.User;
import com.wcy.wojbackendpostservice.service.PostCommentThumbService;
import com.wcy.wojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 *

 */
@RestController
@RequestMapping("/comment_thumb")
@Slf4j
public class PostCommentThumbController {

    @Resource
    private PostCommentThumbService postCommentThumbService;

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 点赞 / 取消点赞
     *
     * @param postCommentThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostCommentThumbAddRequest postCommentThumbAddRequest,
                                         HttpServletRequest request) {
        if (postCommentThumbAddRequest == null || postCommentThumbAddRequest.getCommentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userFeignClient.getLoginUser(request);
        long commentId = postCommentThumbAddRequest.getCommentId();
        int result = postCommentThumbService.doPostCommentThumb(commentId, loginUser);
        return ResultUtils.success(result);
    }

}
