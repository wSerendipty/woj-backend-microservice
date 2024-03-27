package com.wcy.wojbackendmodel.model.dto.post;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *

 */
@Data
public class PostUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 特殊标签列表
     */
    private List<String> specialTags;

    private static final long serialVersionUID = 1L;
}