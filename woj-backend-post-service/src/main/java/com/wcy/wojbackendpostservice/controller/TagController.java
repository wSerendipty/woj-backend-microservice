package com.wcy.wojbackendpostservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wcy.wojbackendcommon.common.BaseResponse;
import com.wcy.wojbackendcommon.common.ErrorCode;
import com.wcy.wojbackendcommon.common.ResultUtils;
import com.wcy.wojbackendcommon.exception.ThrowUtils;
import com.wcy.wojbackendmodel.model.dto.tag.TagAddRequest;
import com.wcy.wojbackendmodel.model.dto.tag.TagQueryRequest;
import com.wcy.wojbackendmodel.model.entity.Tag;
import com.wcy.wojbackendmodel.model.vo.TagVO;
import com.wcy.wojbackendpostservice.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/8 9:57
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Resource
    private TagService tagService;

    @PostMapping("/add")
    public BaseResponse<String> addTag(@RequestBody TagAddRequest tagAddRequest) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagAddRequest, tag);
        boolean save = tagService.save(tag);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("添加成功");
    }

    @PostMapping("/list")
    public BaseResponse<List<TagVO>> listTag(@RequestBody TagQueryRequest tagQueryRequest) {
        QueryWrapper<Tag> queryWrapper = tagService.getQueryWrapper(tagQueryRequest);
        return ResultUtils.success(tagService.listTagVO(queryWrapper));
    }

}
