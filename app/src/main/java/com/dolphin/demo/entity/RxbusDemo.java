package com.dolphin.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * rxbus 演示实体
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/6
 */
@Data
@Accessors
public class RxbusDemo {

    private Integer id;

    private String title;

    private String description;

}
