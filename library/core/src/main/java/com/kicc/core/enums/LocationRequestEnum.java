package com.kicc.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<p>
 * 位置请求状态枚举
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/28
 */
@Getter
@RequiredArgsConstructor
public enum LocationRequestEnum {

    NEW(-1),

    TERMINATED(0),

    RUNNABLE(1);

    private final Integer status;

}
