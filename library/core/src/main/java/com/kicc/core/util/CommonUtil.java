package com.kicc.core.util;

import lombok.experimental.UtilityClass;

/**
 *<p>
 * 通用工具
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/4
 */
@UtilityClass
public class CommonUtil {

	/** 获取url最后一段 */
	public static String getUrlLast(String url) {
		if (url == null || url.length() == 0) return "";
		int nameStart = url.lastIndexOf('/') + 1;
		return url.substring(nameStart);
	}

}
