package com.wcy.wojbackendmodel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TagVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签所属类型（question/post）
     */
    private String belongType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}