package com.sui.haedal.common.page;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 分页工具
 *
 * @author stelylan
 */
@Data
@Accessors(chain = true)
public class BasePageQuery {
	/**
	 * 当前页
	 */
	private Integer current;

	/**
	 * 每页的数量
	 */
	private Integer size;

	/**
	 * 升序排序字段名
	 */
	private String colAscOrder;

	/**
	 * 降序排序字段名
	 */
	private String colDescOrder;
}
