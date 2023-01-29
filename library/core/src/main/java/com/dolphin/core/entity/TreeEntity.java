package com.dolphin.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 树结构模型
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2021/12/28
 */
@Data
@Accessors
public class TreeEntity<T> extends CommonEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 父级编号 **/
    private String parentId;

    /** 名称 */
    protected String name;

    /** 排序 **/
    private Integer sort;

    protected List<T> children = new ArrayList();

}
