package com.sui.haedal.common.page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;

/**
 * 分页工具
 *
 * @author stelylan
 */
public class Condition {
	/**
	 * 转化成mybatis plus中的Page
	 *
	 * @param query 查询条件
	 * @return IPage
	 */
	public static <T> IPage<T> getPage(BasePageQuery query) {
		Page<T> page = new Page<>(toInt(query.getCurrent(), 1), toInt(query.getSize(), 10));
		if (isNotBlank(query.getColDescOrder())) {
			page.addOrder(OrderItem.desc(SqlKeyword.filter(query.getColDescOrder())));
		}
		if (isNotBlank(query.getColAscOrder())) {
			page.addOrder(OrderItem.asc(SqlKeyword.filter(query.getColAscOrder())));
		}
		return page;
	}


	public static boolean isNotBlank(final CharSequence cs) {
		return StringUtils.hasText(cs);
	}
	public static int toInt(Integer val, final int defaultValue) {
		if (val == null) {
			return defaultValue;
		}
		return val;
	}
	/**
	 * 转化成mybatis plus中的Page, 支持多字段排序
	 *
	 * @param query 查询条件
	 * @return IPage
	 */
	public static <T> IPage<T> getMultiPage(BasePageQuery query) {
		Page<T> page = new Page<>(toInt(query.getCurrent(), 1),toInt(query.getSize(), 10));
		if (isNotBlank(query.getColDescOrder())) {
			for (String item : query.getColDescOrder().split(StringPool.COMMA)) {
				page.addOrder(OrderItem.desc(SqlKeyword.filter(item)));
			}
		}
		if (isNotBlank(query.getColAscOrder())) {
			for (String item : query.getColAscOrder().split(StringPool.COMMA)) {
				page.addOrder(OrderItem.asc(SqlKeyword.filter(item)));
			}
		}
		return page;
	}

	/**
	 * 转化成mybatis plus中的Page, 支持多字段排序
	 *
	 * @param query 查询条件
	 * @return IPage
	 */
	public static <T> IPage<T> getMultiPageByIgnoreNull(BasePageQuery query) {
		Page<T> page = new Page<>(toInt(query.getCurrent(), 1), toInt(query.getSize(), 10));
		if (isNotBlank(query.getColDescOrder())) {
			for (String item : query.getColDescOrder().split(StringPool.COMMA)) {
				page.addOrder(OrderItem.desc("ifnull(" + SqlKeyword.filter(item) + ", '')"));
			}
		}
		if (isNotBlank(query.getColAscOrder())) {
			for (String item : query.getColAscOrder().split(StringPool.COMMA)) {
				page.addOrder(OrderItem.asc("ifnull(" + SqlKeyword.filter(item) + ", '')"));
			}
		}
		return page;
	}

	/**
	 * 转换为mybatis plus中的Page
	 * @param query
	 * @param queryWrapper
	 * @return
	 * @param <T>
	 */
	public static <T> IPage<T> getPageOrderByIgnoreNull(BasePageQuery query, QueryWrapper<T> queryWrapper) {
		Page<T> page = new Page<>(query.getCurrent(), query.getSize());
		if (isNotBlank(query.getColDescOrder())) {
			queryWrapper.orderByDesc(query.getColDescOrder()).orderByDesc(query.getColDescOrder() + " IS NULL");
		}
		if (isNotBlank(query.getColAscOrder())) {
			queryWrapper.orderByAsc(query.getColAscOrder()).orderByAsc(query.getColAscOrder() + " IS NULL");
		}
		return page;
	}

	public static <T, N> IPage<N> copyNew(IPage<T> page) {
		Page<N> newPage = new Page<>(page.getCurrent(), page.getSize());
		newPage.addOrder(page.orders());
		newPage.setTotal(page.getTotal());
		newPage.setPages(page.getPages());
		return newPage;
	}

	/**
	 * 获取mybatis plus中的QueryWrapper
	 *
	 * @param entity 实体
	 * @param <T>    类型
	 * @return QueryWrapper
	 */
	public static <T> QueryWrapper<T> getQueryWrapper(T entity) {
		return new QueryWrapper<>(entity);
	}
}
