package com.wcy.wojbackenduserservice.controller.inner;

import cn.hutool.json.JSONUtil;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.exception.BusinessException;
import com.wcy.wojbackendcommon.utils.RedisUtil;
import com.wcy.wojbackendmodel.model.entity.User;
import com.wcy.wojbackendserviceclient.service.UserFeignClient;
import com.wcy.wojbackenduserservice.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;


    /**
     * 根据 id 获取用户
     *
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    /**
     * 根据 id 获取用户列表
     *
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }


}
