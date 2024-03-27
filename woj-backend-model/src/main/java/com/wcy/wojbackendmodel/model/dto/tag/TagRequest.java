package com.wcy.wojbackendmodel.model.dto.tag;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/8 9:53
 */
@Data
public class TagRequest implements Serializable {
    /**
     * 标签名称
     */
    private String name;

    private static final long serialVersionUID = 1L;
}
