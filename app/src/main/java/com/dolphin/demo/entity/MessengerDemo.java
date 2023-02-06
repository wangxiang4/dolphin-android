package com.dolphin.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 *<p>
 * 信使 通信演示实体
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/6
 */
@Data
@Accessors
public class MessengerDemo {

    private Integer id;

    private String title;

    private String description;

}
