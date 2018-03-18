package com.ccservice.bus.utils;

public class ObjectUtils {
	/** 
	 * 判断一个类是否为空
	 * @param bean
	 * @return
	 */
	public static boolean isEmpty(Object bean){
		if (null==bean) {
			return true;
		}
		return false;
	}
}
