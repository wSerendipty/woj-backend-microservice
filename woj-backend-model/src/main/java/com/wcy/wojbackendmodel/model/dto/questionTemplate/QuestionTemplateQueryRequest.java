package com.wcy.wojbackendmodel.model.dto.questionTemplate;

import com.wcy.wojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
*
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionTemplateQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long questionId;

    /**
     * 语言
     */
    private String language;


    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}