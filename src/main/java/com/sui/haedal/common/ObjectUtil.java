package com.sui.haedal.common;


import org.springframework.lang.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 对象工具类
 *
 * @author L.cm
 */
public class ObjectUtil extends org.springframework.util.ObjectUtils {

	/**
	 * 判断元素不为空
	 *
	 * @param obj object
	 * @return boolean
	 */
	public static boolean isNotEmpty(@Nullable Object obj) {
		return !ObjectUtil.isEmpty(obj);
	}

	public static boolean isNotArrayType(Object obj) {
		if (obj != null && (obj.getClass().isArray() || obj instanceof Collection)){
			return false;
		}
		return true;
	}

	/**
	 * 判断Object对象为空,判断对象属性空值
	 *
	 * @param obj 判断对象
	 * @return Boolean
	 */
	public static Boolean isObjectEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof Optional) {
			return !((Optional) obj).isPresent();
		} else if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		} else if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		} else if (obj instanceof Map) {
			return ((Map) obj).isEmpty();
		} else {
			List<Field> fields = getAllField(obj);
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					if (isNotEmpty(field.get(obj)) && !field.getName().equals("serialVersionUID")) {
						return false;
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/***
	 * @description: 获取对象属性值，包含父类的所有属性值
	 * @param: bean
	 * @return: java.util.List<java.lang.reflect.Field>
	 * @author jiang.xu
	 * @date: 2021/7/14 15:30
	 */
	private static List<Field> getAllField(Object bean) {
		Class clazz = bean.getClass();
		List<Field> fields = new ArrayList<>();
		while (clazz != null) {
			fields.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}
}
