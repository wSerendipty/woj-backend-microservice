package com.wcy.wojbackendmodel.model.dto.tag;

import com.wcy.wojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TagQueryRequest extends PageRequest implements Serializable {
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

    private static final long serialVersionUID = 1L;

}