package com.sui.haedal.common;

/**
 * 系统常量
 * @author stelylan
 */
public interface SlipConstants {

	/**
	 * 平台管理员角色id
	 */
	String ROLE_MANAGEMENT_ADMIN = "1123598816738675201";


	/**
	 * 平台管理租户ID
	 */
	String MANAGEMENT_TENANT_ID = "000000";

	/**
	 * 删除状态[0:正常,1:删除]
	 */
	int DB_NOT_DELETED = 0;

	/**
	 * 删除状态[0:正常,1:删除]
	 */
	int DB_IS_DELETED = 1;

	/**
	 * 默认为空消息
	 */
	String DEFAULT_NULL_MESSAGE = "暂无承载数据";

	/**
	 * 默认成功消息
	 */
	String DEFAULT_SUCCESS_MESSAGE = "操作成功";

	/**
	 * 默认失败消息
	 */
	String DEFAULT_FAILURE_MESSAGE = "操作失败";

	/**
	 * 文件上传html-input的name属性对应
	 */
	String FILE_PART = "file";

	/**
	 * 执行链未捕获异常
	 */
	String CHAIN_BUILDER_NOT_CATCH_ERROR = "未捕获异常";

	/**
	 * 执行链非运行时异常错误编码
	 */
	int CHAIN_HANDLER_RUNTIME_EXCEPTION_RESULT_CODE = 10101190;

	/**
	 * 系统机器人用户id
	 */
	Long SYSTEM_ROBOT_USER = 1000000000000000001L;

	/**
	 * 系统机器人用户名字
	 */
	String  SYSTEM_ROBOT_USER_NAME = "系统操作";

}
