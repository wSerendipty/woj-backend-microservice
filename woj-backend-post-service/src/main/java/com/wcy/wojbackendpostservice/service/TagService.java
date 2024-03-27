package com.wcy.wojbackendpostservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.wojbackendmodel.model.dto.tag.TagQueryRequest;
import com.wcy.wojbackendmodel.model.entity.Tag;
import com.wcy.wojbackendmodel.model.vo.TagVO;

import java.util.List;

/**
* @author 王长远
* @description 针对表【tag(标签)】的数据库操作Service
* @createDate 2024-01-08 09:47:20
*/
public interface TagService extends IService<Tag> {

    QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest);

    List<TagVO> listTagVO(QueryWrapper<Tag> queryWrapper);

}
