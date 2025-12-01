package com.sui.haedal.common;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 统一API响应结果封装
 *
 * @author stelylan
 */
@Getter
@Setter
@NoArgsConstructor
public class R<T> {

    /**
     * 返回类型
     *
     * @ignore
     */
    private Class<?> resultType;

    private int code;

    private boolean success;

    private T data;

    private String msg;

    private R(IResultCode resultCode) {
        this(resultCode, null, resultCode.getMessage());
    }

    private R(IResultCode resultCode, String message) {
        this(resultCode, null, message);
    }

    private R(IResultCode resultCode, String message, T data) {
        this(resultCode, data, message);
    }

    private R(IResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMessage());
    }

    private R(IResultCode resultCode, T data, String message) {
        this(resultCode.getCode(), data, message);
    }

    private R(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.msg = message;
        this.success = ResultCode.SUCCESS.getCode() == code;
    }

    /**
     * 返回R
     *
     * @param data
     * @param <T>  T
     * @return R
     */
    public static <T> R<T> data(T data) {
        return data(data, SlipConstants.DEFAULT_SUCCESS_MESSAGE);
    }

    /**
     * 返回R
     *
     * @param data
     * @param msg
     * @param <T>  T
     * @return R
     */
    public static <T> R<T> data(T data, String msg) {
        return data(HttpServletResponse.SC_OK, data, msg);
    }

    /**
     * 返回R
     *
     * @param code
     * @param data
     * @param msg
     * @param <T> T
     * @return R
     */
    public static <T> R<T> data(int code, T data, String msg) {
        return new R<T>(code, wrapperReturn(data), checkEmpty(data) ? SlipConstants.DEFAULT_NULL_MESSAGE : msg);
    }

    /**
     * 返回R
     *
     * @param msg
     * @return R
     */
    public static R success(String msg) {
        return new R<>(ResultCode.SUCCESS, msg);
    }

    /**
     * 返回R
     *
     * @param msg
     * @return R
     */
    public static R success(String msg, Map<String, Object> data) {
        return new R<>(ResultCode.SUCCESS, msg, data);
    }

    /**
     * 返回R
     *
     * @param resultCode
     * @return
     */
    public static R success(IResultCode resultCode) {
        return new R<>(resultCode);
    }

    /**
     * 返回R
     *
     * @param resultCode
     * @return
     */
    public static R success(IResultCode resultCode, Map<String, Object> data) {
        return new R<>(resultCode, data);
    }

    /**
     * 返回R
     *
     * @param resultCode
     * @param msg
     * @return
     */
    public static R success(IResultCode resultCode, String msg) {
        return new R<>(resultCode, msg);
    }

    /**
     * 返回R
     *
     * @param resultCode
     * @param msg
     * @return
     */
    public static R success(IResultCode resultCode, String msg, Map<String, Object> data) {
        return new R<>(resultCode, msg, data);
    }

    /**
     * 返回R
     *
     * @param msg
     * @return R
     */
    public static R fail(String msg) {
        return new R<>(ResultCode.FAILURE, msg);
    }

    /**
     * 返回R
     *
     * @param msg
     * @param failData
     * @return
     */
    public static R fail(String msg, Map<String, Object> failData) {
        return new R<>(ResultCode.FAILURE, failData, msg);
    }

    /**
     * 返回R
     *
     * @param code 状态码
     * @param msg  消息
     * @return
     */
    public static R fail(int code, String msg) {
        return new R<>(code, null, msg);
    }

    /**
     * 返回R
     *
     * @param code
     * @param msg
     * @param failData
     * @return R
     */
    public static R fail(int code, String msg, Map<String, Object> failData) {
        return new R<>(code, failData, msg);
    }

    /**
     * 返回R
     * @param resultCode
     * @return
     */
    public static R fail(IResultCode resultCode) {
        return new R<>(resultCode);
    }

    /**
     * 返回R
     *
     * @param resultCode
     * @param failData
     * @return
     */
    public static R fail(IResultCode resultCode, Map<String, Object> failData) {
        return new R<>(resultCode, failData);
    }

    /**
     * 返回R
     *
     * @param resultCode
     * @param msg
     * @return
     */
    public static R fail(IResultCode resultCode, String msg) {
        return new R<>(resultCode, msg);
    }

    /**
     * 返回R
     *
     * @param resultCode
     * @param msg
     * @param failData
     * @return R
     */
    public static R fail(IResultCode resultCode, String msg, Map<String, Object> failData) {
        return new R<>(resultCode, msg, failData);
    }

    /**
     * 返回R
     *
     * @param flag 成功状态
     * @return R
     */
    public static <T> R<T> status(boolean flag) {
        return flag ? success(SlipConstants.DEFAULT_SUCCESS_MESSAGE) : fail(SlipConstants.DEFAULT_FAILURE_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    public void setResultObject(Object object) {
        this.setData((T) object);
    }

    @Override
    public String toString() {
        String strResult = null;

        if (Objects.nonNull(this.data)) {
            strResult = JSON.toJSONString(this.data);

            if (strResult.length() > 100) {
                strResult = strResult.substring(0, 100) + "...";
            }
        }

        return "Response.length=" + (Objects.isNull(strResult) ? 0 : strResult.length()) +
                ",,code=" + this.code +
                ",,message=" + this.msg +
                ",,result=" + strResult;
    }

    /**
     * 校验是否为空
     * @param data
     * @return
     */
    private static <T> boolean checkEmpty(T data) {
        if (Objects.isNull(data)) {
            return true;
        }

        if (CollectionUtils.isArray(data)) {
            return Array.getLength(data) == 0;
        }

        if (data instanceof Collection<?>) {
            return CollectionUtils.isEmpty((Collection<?>) data);
        }

        if (data instanceof Map<?, ?>) {
            return CollectionUtils.isEmpty((Map<?, ?>) data);
        }

        return false;
    }

    /**
     * 校验空对象
     * @param data
     * @param <T>
     * @return
     */
    private static <T> T wrapperReturn(T data) {
        if (Objects.isNull(data)) {
            return null;
        }

//        if (CollectionUtil.isArray(data)
//            || data instanceof Collection<?>
//            || data instanceof Map<?, ?>) {
//            // TODO 跟普通对象处理方式不变
//        }

        return (T) data;
    }
}
