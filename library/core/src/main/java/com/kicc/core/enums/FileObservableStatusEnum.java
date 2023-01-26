package com.kicc.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<p>
 * 文件状态枚举
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/28
 */
@Getter
@RequiredArgsConstructor
public enum FileObservableStatusEnum {

    FAIL(-1),

    SUCCESS(0),

    SUSPEND(1),

    RUNNABLE(2);

    private final Integer status;

}
